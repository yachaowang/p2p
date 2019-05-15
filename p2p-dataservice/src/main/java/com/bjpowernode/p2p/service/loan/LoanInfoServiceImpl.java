package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("loanInfoServiceImpl")
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Override
    public Double queryHistoryAverageRate() {
        //从redis缓存中获取历史平均年化收益率
        Double historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);
        //判断是否有值
        if(historyAverageRate==null){
            //去数据库查询
           historyAverageRate = loanInfoMapper.selectHistoryAverageRate();

            //并存放到redis缓存中
            redisTemplate.opsForValue().set(Constants.HISTORY_AVERAGE_RATE,historyAverageRate,15, TimeUnit.MINUTES);
        }
        return historyAverageRate;
    }

    @Override
    public List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap) {

        return loanInfoMapper.selectLoanInfoByPage(paramMap);
    }

    @Override
    public PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap) {
        PaginationVO<LoanInfo> paginationVO = new PaginationVO<>();

        Long total = loanInfoMapper.selectTotal(paramMap);
        paginationVO.setTotal(total);
        List<LoanInfo> loanInfos = loanInfoMapper.selectLoanInfoByPage(paramMap);
        paginationVO.setDataList(loanInfos);
        return paginationVO;
    }

    @Override
    public LoanInfo queryLoanInfoById(Integer id) {

        return loanInfoMapper.selectByPrimaryKey(id);
    }

}
