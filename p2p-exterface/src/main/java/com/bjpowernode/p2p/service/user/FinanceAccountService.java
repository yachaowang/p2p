package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.FinanceAccount;

/**
 * @ClassName FinanceAccountService
 * @Description
 * @Version 1.0
 * @Date 2019/3/29 10:35
 * @Author wyc
 **/
public interface FinanceAccountService {
    /**
     * 根据用户ID查询金额
     * @param uid
     * @return
     */
    FinanceAccount queryFinanceAccountByUid(Integer uid);
}
