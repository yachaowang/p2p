package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.vo.BidUserTop;
import com.bjpowernode.p2p.service.vo.PaginationVO;
import com.bjpowernode.p2p.service.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName BidInfoServiceImpl
 * @Description
 * @Version 1.0
 * @Date 2019/3/26 14:26
 * @Author wyc
 **/
@Service("bidInfoServiceImpl")
public class BidInfoServiceImpl implements BidInfoService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public Double queryAllBidMoney() {
        //获取指定key的操作对象
        BoundValueOperations<String, Object> boundValueOperations = redisTemplate.boundValueOps(Constants.ALL_BID_MONEY);

        //获取该操作对象所对应的value
        Double allBidMoney = (Double) boundValueOperations.get();

        if(null==allBidMoney){
            //去数据库查询
            allBidMoney = bidInfoMapper.selectAllBidMoney();

            //将该值存放到redis缓存中,并设置失效时间
            boundValueOperations.set(allBidMoney,15, TimeUnit.MINUTES);
        }
        return allBidMoney;
    }

    @Override
    public List<BidInfo> queryBidInfoListByLoanId(Integer id) {

        return bidInfoMapper.selectBidInfoListByLoanId(id);
    }

    @Override
    public ResultObject invest(Map<String, Object> paramMap) {
        ResultObject resultObject = new ResultObject();
        resultObject.setErrorCode(Constants.SUCCESS);
        Integer loanId = (Integer) paramMap.get("loanId");
        Integer uid = (Integer) paramMap.get("uid");
        Double bidMoney = (Double) paramMap.get("bidMoney");
        String phone = (String) paramMap.get("phone");

        //更新产品的剩余可投资金额
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        //通过数据库乐观锁机制来解决这个问题
        paramMap.put("version",loanInfo.getVersion());
        int updateLeftProductMoneyCount = loanInfoMapper.updateLeftProductMoneyByLoanId(paramMap);
        if(updateLeftProductMoneyCount>0){
            //更新帐户可用余额
            int updateFinanceCount = financeAccountMapper.updateFinanceAccountByInvest(paramMap);

            if(updateFinanceCount>0){
                //新增投资记录
                BidInfo bidInfo = new BidInfo();
                bidInfo.setUid(uid);
                bidInfo.setLoanId(loanId);
                bidInfo.setBidMoney(bidMoney);
                bidInfo.setBidTime(new Date());
                bidInfo.setBidStatus(1);
                int insertBidInfoCount = bidInfoMapper.insertSelective(bidInfo);
                if(insertBidInfoCount>0){
                    //获取到产品的信息
                 LoanInfo loanDetail = loanInfoMapper.selectByPrimaryKey(loanId);
                 if(0==loanDetail.getLeftProductMoney()){
                     //更新产品的状态为1已满标和满标时间
                     LoanInfo updateLoanInfo = new LoanInfo();
                     updateLoanInfo.setId(loanId);
                     updateLoanInfo.setProductStatus(1);
                     updateLoanInfo.setProductFullTime(new Date());

                     int updateCount = loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);

                     if(updateCount<=0){
                         resultObject.setErrorCode(Constants.FAIL);
                     }
                 }


                    //将用户的投资金额累加到redis缓存中
                    redisTemplate.opsForZSet().incrementScore(Constants.INVEST_TOP,phone,bidMoney);


                }else{
                    resultObject.setErrorCode(Constants.FAIL);
                }

            }else {

                resultObject.setErrorCode(Constants.FAIL);
            }
        }else{
            resultObject.setErrorCode(Constants.FAIL);
        }
        return resultObject;
    }

    @Override
    public List<BidInfo> queryBidIcnfoListByUid(Map<String, Object> paramMap) {
        return bidInfoMapper.selectBidInfoByPage(paramMap);
    }

    @Override
    public PaginationVO<BidInfo> queryBidInfoByPage(Map<String, Object> paramMap) {
        PaginationVO<BidInfo> paginationVO = new PaginationVO<>();

        paginationVO.setTotal(bidInfoMapper.selectTotal(paramMap));

        paginationVO.setDataList(bidInfoMapper.selectBidInfoByPage(paramMap));


        return paginationVO;
    }

    @Override
    public List<BidUserTop> queryBidUserTop() {
        List<BidUserTop> bidUserTopList = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(Constants.INVEST_TOP, 0, 5);

        Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTuples.iterator();
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Object> next = iterator.next();
            String phone = (String) next.getValue();
            Double score = next.getScore();

            BidUserTop bidUserTop = new BidUserTop();
            bidUserTop.setPhone(phone);
            bidUserTop.setScore(score);

            bidUserTopList.add(bidUserTop);
        }


        return bidUserTopList;
    }
}
