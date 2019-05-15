package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName UserServiceImpl
 * @Description
 * @Version 1.0
 * @Date 2019/3/26 9:50
 * @Author wyc
 **/
@Service("userServiceImpl")
public class UserServiceImpl implements UserService{

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public Long queryAllUserCount() {
        //从redis缓存中取数据
        Long allUserCount = (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);

        if(null==allUserCount){
            //从数据库中取数据
            allUserCount = userMapper.selectAllUserCount();
            //放到redis缓存中
            redisTemplate.opsForValue().set(Constants.ALL_USER_COUNT,allUserCount,15, TimeUnit.SECONDS);
        }
        return allUserCount;
    }

    @Override
    public User queryUserPhone(String phone) {

        return userMapper.selectUserByPhone(phone);
    }

    @Override
    public ResultObject register(String phone, String loginPassword) {
        ResultObject resultObject = new ResultObject();
        resultObject.setErrorCode(Constants.SUCCESS);

        //新增用户
        User user = new User();
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        user.setAddTime(new Date());
        user.setLastLoginTime(new Date());

        int insertUserCount = userMapper.insertSelective(user);
        if(insertUserCount>0){
            User userDetail = userMapper.selectUserByPhone(phone);
            //开立账户
            FinanceAccount financeAccount = new FinanceAccount();
            financeAccount.setUid(userDetail.getId());
            financeAccount.setAvailableMoney(888.0);

            int insertFinanceCount = financeAccountMapper.insertSelective(financeAccount);
            if(insertFinanceCount<=0){
                resultObject.setErrorCode(Constants.FAIL);
            }
        }else{
            resultObject.setErrorCode(Constants.FAIL);
        }

        return resultObject;
    }


    @Override
    public int modifyUserById(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User login(String phone, String loginPassword) {
        //根据手机号和密码查询用户信息
        User user = userMapper.selectUserByPhoneAndLoginPassword(phone,loginPassword);
        ////判断用户是否存在
        if(null != user){
            //更新最近登录时间
            User updateUser = new User();
            updateUser.setId(user.getId());
            updateUser.setLastLoginTime(new Date());
            userMapper.updateByPrimaryKeySelective(updateUser);
        }
        return user;
    }

}
