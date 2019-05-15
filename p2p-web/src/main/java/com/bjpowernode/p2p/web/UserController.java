package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.http.HttpClientUtils;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.bjpowernode.p2p.service.loan.RedisService;
import com.bjpowernode.p2p.service.user.UserService;
import com.bjpowernode.p2p.service.vo.ResultObject;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName UserController
 * @Description
 * @Version 1.0
 * @Date 2019/3/28 17:48
 * @Author wyc
 **/
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private FinanceAccountService financeAccountService;

    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private BidInfoService bidInfoService;

    @Autowired
    private RedisService redisService;

    @RequestMapping(value = "/loan/checkPhone")
    @ResponseBody
    public Object checkPhone(HttpServletRequest request,
                             @RequestParam(value = "phone",required = true) String Phone){
        Map<String,Object> retMap = new HashMap<>();
        //根据手机号查询用户信息(手机号) -> 返回User信息
        User user = userService.queryUserPhone(Phone);

        if(null!=user){
            retMap.put(Constants.ERROR_MESSAGE,"手机号已存在，请更换手机号");
            return retMap;
        }
        retMap.put(Constants.ERROR_MESSAGE,Constants.OK);
        return retMap;
    }

    //图片验证码
    @GetMapping(value = "/loan/checkCaptcha")
    @ResponseBody
    public Map<String,Object> checkCaptcha(HttpServletRequest request,
                                           @RequestParam(value = "captcha",required = true) String captcha){
        Map<String,Object> retMap = new HashMap<String,Object>();

        //从session中获取图形验证码
        String sessioncaptcha  = (String) request.getSession().getAttribute(Constants.CAPTCHA);

        //用户输入的与session中的做对比
        if(!StringUtils.equalsIgnoreCase(captcha,sessioncaptcha)){
            retMap.put(Constants.ERROR_MESSAGE,"请输入正确的图形验证码");
            return retMap;
        }
        retMap.put(Constants.ERROR_MESSAGE,Constants.OK);
         return retMap;
    }
    //注册
    @RequestMapping(value = "/loan/register")
    @ResponseBody
    public Map<String,Object> register(HttpServletRequest request,
                                       @RequestParam (value = "phone",required = true) String phone,
                                       @RequestParam (value = "loginPassword",required = true) String loginPassword){
        Map<String,Object> retMap = new HashMap<String,Object>();

        //用户注册
        ResultObject resultObject = userService.register(phone,loginPassword);

        if(StringUtils.equals(resultObject.getErrorCode(),Constants.SUCCESS)){
            //将用户的信息存放到session中
            request.getSession().setAttribute(Constants.SESSION_USER,userService.queryUserPhone(phone));
            retMap.put(Constants.ERROR_MESSAGE,Constants.OK);
        }else{
            request.getSession().setAttribute(Constants.ERROR_MESSAGE,"注册失败");
            return retMap;
        }
        return retMap;
    }
    //获取金额
    @RequestMapping(value = "/loan/myFinanceAccount")
    @ResponseBody
    public FinanceAccount myFinanceAccount(HttpServletRequest request){
        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        //根据用户标识获取帐户资金信息
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());

        return financeAccount;
    }

    //实名认证
    @RequestMapping(value = "/loan/verifyRealName")
    @ResponseBody
    public Map<String,Object> verifyRealName(HttpServletRequest request,
                                             @RequestParam (value = "idCard",required = true) String idCard,
                                             @RequestParam (value = "realName",required = true) String realName) throws Exception {
        Map<String,Object> retMap = new HashMap<>();

        //准备接口需要的参数
        Map<String,Object> paramMap = new HashMap<>();
        //使用京东万象接口平台,需要申请appkey
        paramMap.put("appkey","41941804d53aa6d88a663c0a2a1");
        //真实姓名
        paramMap.put("realName",realName);
        //身份证号码
        paramMap.put("cardNo",idCard);

        //调用互联网接口,来对用户输入的真实姓名和身份证号码进行匹配认证
        String jsonString = (String) HttpClientUtils.doPost("https://way.jd.com/youhuoBeijing/test",paramMap);
        /*String jsonString = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"error_code\": 0,\n" +
                "        \"reason\": \"成功\",\n" +
                "        \"result\": {\n" +
                "            \"realname\": \"乐天磊\",\n" +
                "            \"idcard\": \"350721197702134399\",\n" +
                "            \"isok\": true\n" +
                "        }\n" +
                "    }\n" +
                "}";
*/
        //将json格式的字符串转换为json对象
       JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //获取通信标识code
        String code =jsonObject.getString("code");
        //判断是否通信成功
        if(StringUtils.equals("10000",code)){
            //实名认证的结果是用户信息匹配isok
            Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");
            //判断是否匹配
            if(isok){
                //从session中获取用户信息
                User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);

                //将用户的身份证号码和真实姓名更新到用户信息
                User updateUser = new User();
                updateUser.setId(user.getId());
                updateUser.setName(realName);
                updateUser.setIdCard(idCard);

                int updateUserCount = userService.modifyUserById(updateUser);
                if(updateUserCount>0){
                    //更新session中用户的信息
                    user.setName(realName);
                    user.setIdCard(idCard);
                    request.getSession().setAttribute(Constants.SESSION_USER,user);
                    retMap.put(Constants.ERROR_MESSAGE,Constants.OK);
                } else{
                    retMap.put(Constants.ERROR_MESSAGE,"实名认证失败,请重试");
                    return retMap;
                }
            }else {
                retMap.put(Constants.ERROR_MESSAGE,"实名认证失败,请重试");
                return retMap;
            }
        } else {
            retMap.put(Constants.ERROR_MESSAGE,"实名认证失败,请重试");
            return retMap;
        }
        return retMap;
    }
    //退出
    @RequestMapping(value = "/loan/logout")
    public String logout(HttpServletRequest request){
        //清空Session
        request.getSession().invalidate();
        return "redirect:/index";
    }

    //获取登录界面的历史年化收益率，平台用户数，累计成交额，
    @RequestMapping(value = "/loan/loadStat")
    @ResponseBody
    public Object loadStat(HttpServletRequest request){
        Map<String,Object> retMap = new HashMap<>();

        //历史平均年化收益率
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();

        //平台注册总人数
        Long allUserCount = userService.queryAllUserCount();

        //平台累计投资金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();

        retMap.put(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);
        retMap.put(Constants.ALL_USER_COUNT,allUserCount);
        retMap.put(Constants.ALL_BID_MONEY,allBidMoney);

        return retMap;
    }
    //短信验证码
    @RequestMapping(value = "/loan/messageCode")
    @ResponseBody
    public Object messageCode(HttpServletRequest request,
                              @RequestParam(value = "phone",required = true)String phone) throws Exception {
        Map<String,Object> retMap = new HashMap<String,Object>();

        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("appkey","9743a39c35c65d3741bc3dcf84ff54bc");
        paramMap.put("mobile",phone);

        //生成随机数字
        String randomNumber = this.getRandomNumber(4);
        //短信内容=短信签名+短信正文(随机数字)
        String content = "【凯信通】您的验证码是："+randomNumber;
        paramMap.put("content",content);

        //发送短信
        //String jsonString = HttpClientUtils.doPost("https://way.jd.com/kaixintong/kaixintong", paramMap);
        String jsonString = "{\"code\":\"10000\",\"charge\":false,\"remain\":0,\"msg\":\"查询成功\",\"result\":\"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-677327</remainpoint>\\n <taskID>84572508</taskID>\\n <successCounts>1</successCounts></returnsms>\"}";
        //使用fastjson解析json格式的字符串
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        //获取通信标识code
        String code = jsonObject.getString("code");

        //判断通信是否成功
        if(StringUtils.equals("10000",code)){
            //获取result,但是result值是一个xml格式的字符串
           String resultXml = jsonObject.getString("result");

           //将xml格式的字符串转换为Dom对象
            Document document = DocumentHelper.parseText(resultXml);
            //通过xpath路径表达式来获取指定的节点对象
            //xpath路径表达式:    /returnsms/returnstatus  || //returnstatus
            Node node = document.selectSingleNode("//returnstatus");
            //获取节点对象的文本内容
            String returnstatus = node.getText();

            //判断是否发送成功
            if(StringUtils.equals("Success",returnstatus)){
                //存储随机数字
                redisService.put(phone,randomNumber);
                retMap.put("randomNumber",randomNumber);
                retMap.put(Constants.ERROR_MESSAGE,Constants.OK);
            }else{
                retMap.put(Constants.ERROR_MESSAGE,"获取短信验证码失败");
                return retMap;
            }
        } else {
            retMap.put(Constants.ERROR_MESSAGE,"获取短信验证码失败");
            return retMap;
        }

        return retMap;
    }

    private String getRandomNumber(int count) {
        String[] arr = {"0","1","2","3","4","5","6","7","8","9"};
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0;i<count;i++){
            //round() 方法可把一个数字舍入为最接近的整数
            int index = (int) Math.round(Math.random()*9);
            stringBuilder.append(arr[index]);
        }
        return stringBuilder.toString();
    }

    //登录
    @RequestMapping(value = "/loan/login")
    @ResponseBody
    public Object login(HttpServletRequest request,
                        @RequestParam(value = "phone",required = true) String phone,
                        @RequestParam(value = "loginPassword",required = true) String loginPassword,
                        @RequestParam(value = "messageCode",required = true) String messageCode){
        Map<String,Object> retMap = new HashMap<>();

        //从redis中获取phone所对应的值
        String redisMessageCode = redisService.get(phone);
        //判断短信验证码是否一致
        if(StringUtils.equals(redisMessageCode,messageCode)){
            //用户登录:根据手机号和密码查询用户信息
            User user = userService.login(phone,loginPassword);
            //判断用户是否存在
            if(null==user){
                retMap.put(Constants.ERROR_MESSAGE,"手机号或密码有误,请重新输入");
                return retMap;
            }
            //将用户的信息存放到session中
            request.getSession().setAttribute(Constants.SESSION_USER,user);
            retMap.put(Constants.ERROR_MESSAGE,Constants.OK);

        }else{
                retMap.put(Constants.ERROR_MESSAGE,"请输入正确的短信验证码");
                return retMap;
        }
        return retMap;

    }
    //进入我的小金库
    @RequestMapping(value = "/loan/myCenter")
    public String myCenter(HttpServletRequest request, Model model){
        //从session中获取用户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());
        //将以下查询看作是一个分页,用户标识,页码,每页显示条数
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("uid",sessionUser.getId());
        paramMap.put("currentPage",0);
        paramMap.put("pageSize",5);

        //根据用户标识获取最近投资记录
        List<BidInfo> bidInfoList = bidInfoService.queryBidIcnfoListByUid(paramMap);

        model.addAttribute("financeAccount",financeAccount);
        model.addAttribute("bidInfoList",bidInfoList);

        return "myCenter";
    }


}
