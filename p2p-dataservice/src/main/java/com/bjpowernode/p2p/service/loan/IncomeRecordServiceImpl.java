package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.common.util.DateUtils;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName IncomeRecordServiceImpl
 * @Description
 * @Version 1.0
 * @Date 2019/4/1 20:23
 * @Author wyc
 **/
@Service("IncomeRecordServiceImpl")
public class IncomeRecordServiceImpl implements IncomeRecordService{
    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public void generateIncomePlan() {
        //查询已满标的产品 -> 返回List<已满标产品>
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoListByProductStatus(1);

        //循环遍历,获取到每一个产品
        for(LoanInfo loanInfo : loanInfoList){
            //获取到当前产品的所有投资记录 -> 返回List<投资记录>
            List<BidInfo> bidInfoList = bidInfoMapper.selectBidInfosLoanId(loanInfo.getId());
            //循环遍历List<投资记录>,获取到每一条投资记录
            for(BidInfo bidInfo : bidInfoList){
                //将每一条投资记录生成对应的收益计划
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setLoanId(loanInfo.getId());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setIncomeStatus(0);

                //收益时间Date
                Date incomeDate = null;
                //收益金额
                Double incomeMoney = null;

                if(Constants.PRODUCT_TYPE_X==loanInfo.getProductType()){
                    //新手宝
                    incomeDate = DateUtils.getDateByAddDays(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle();
                }else{
                    //优选或散标
                    incomeDate = DateUtils.getDateByAddMonths(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle()*30;
                }
                incomeMoney = Math.round(incomeMoney*Math.pow(10,2))/Math.pow(10,2);
                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeMoney(incomeMoney);

                incomeRecordMapper.insertSelective(incomeRecord);
            }
            //更新产品的状态为2满标且生成收益计划
            LoanInfo updateLoanInfo = new LoanInfo();
            updateLoanInfo.setId(loanInfo.getId());
            updateLoanInfo.setProductStatus(2);
            loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
        }

    }
    //收益返还
    @Override
    public void generateIncomeBack() {
        //获取到收益时间与当前时间一致且收益状态为0未返还 -> 返回List<收益记录>
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordByIncomeStatusAndCurdate(0);
        //循环遍历List,将每条收益返还给对应的帐户资金
        for (IncomeRecord incomeRecord : incomeRecordList){
            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("uid",incomeRecord.getUid());
            paramMap.put("bidMoney",incomeRecord.getBidMoney());
            paramMap.put("incomeMoney",incomeRecord.getIncomeMoney());

            int updateCount = financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);

            if(updateCount>0){
                //更新当前收益记录的状态为1
                IncomeRecord updateIncomeRecord = new IncomeRecord();
                updateIncomeRecord.setId(incomeRecord.getId());
                updateIncomeRecord.setIncomeStatus(1);
                incomeRecordMapper.updateByPrimaryKeySelective(updateIncomeRecord);
            }
        }
    }
}
