package com.bjpowernode.p2p.service.loan;


import com.bjpowernode.p2p.model.loan.RechargeRecord;

import java.util.Map;

/**
 * @ClassName RechargeRecordService
 * @Description
 * @Version 1.0
 * @Date 2019/4/2 19:28
 * @Author wyc
 **/
public interface RechargeRecordService {


    /**
     * 添加充值记录
     * @param rechargeRecord
     * @return
     */
    int addRechargeRecord(RechargeRecord rechargeRecord);

    /**
     * 根据用户表示更新充值记录
     * @param rechargeRecord
     * @return
     */
    int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord);

    /**
     *用户充值
     * @param paramMap
     * @return
     */
    int recharge(Map<String, Object> paramMap);
}
