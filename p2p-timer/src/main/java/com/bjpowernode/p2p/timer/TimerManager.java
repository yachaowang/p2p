package com.bjpowernode.p2p.timer;

import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ClassName:TimerManager
 * Package:com.bjpowernode.p2p.timer
 * Description:
 *
 * @date:2019/4/1 10:31
 * @author:guoxin
 */
@Component
public class TimerManager {
    @Autowired
    private IncomeRecordService incomeRecordService;
    private Logger logger = LogManager.getLogger(TimerManager.class);

    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomePlan() {
        logger.info("--------------生成收益计划开始--------------");
        incomeRecordService.generateIncomePlan();
        logger.info("--------------生成收益计划结束--------------");

    }
    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomeBack() {
        logger.info("--------------收益返还开始--------------");
        incomeRecordService.generateIncomeBack();
        logger.info("--------------收益返还结束--------------");

    }
}
