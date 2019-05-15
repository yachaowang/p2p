package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.vo.ResultObject;

public interface UserService {
    /**
     * 获取平台总人数
     * @return
     */
    Long queryAllUserCount();

    /**
     *根据手机号查询用户
     * @param phone
     * @return
     */
    User queryUserPhone(String phone);

    /**
     * 用户注册
     * @param phone
     * @param loginPassword
     * @return
     */
    ResultObject register(String phone, String loginPassword);

    /**
     * 根据用户id更新用户信息
     * @param user
     * @return
     */
    int modifyUserById(User user);

    /**
     * 根据手机号，密码查询用户
     * @param phone
     * @param loginPassword
     * @return
     */
    User login(String phone, String loginPassword);
}
