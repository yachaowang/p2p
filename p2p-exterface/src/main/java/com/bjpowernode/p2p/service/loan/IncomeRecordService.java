package com.bjpowernode.p2p.service.loan;

public interface IncomeRecordService {
    /**
     * 生成收益计划
     */
    void generateIncomePlan();

    /**
     * 收益返还计划
     */
    void generateIncomeBack();
}
