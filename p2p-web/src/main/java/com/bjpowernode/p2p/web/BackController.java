package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.http.HttpClientUtils;
import com.bjpowernode.p2p.common.constant.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName BackController
 * @Description
 * @Version 1.0
 * @Date 2019/4/3 13:48
 * @Author wyc
 **/
@Controller
public class BackController {
    //银行卡认证
    @RequestMapping(value = "/loan/back")
    @ResponseBody
    public Map<String, Object> verifyRealName(HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestParam(value = "name", required = true) String name,
                                              @RequestParam(value = "idCard", required = true) String idCard,
                                              @RequestParam(value = "phone", required = true) String phone,
                                              @RequestParam(value = "backId", required = true) String backId) throws Exception {
        Map<String, Object> retMap = new HashMap<>();

        //准备接口需要的参数
        Map<String, Object> paramMap = new HashMap<>();
        //使用京东万象接口平台,需要申请appkey
        paramMap.put("appkey", "9743a39c35c65d3741bc3dcf84ff54bc");
        //真实姓名
        paramMap.put("accName", name);
        //身份证号码
        paramMap.put("certificateNo", idCard);
        paramMap.put("cardPhone", phone);
        paramMap.put("cardNo", backId);

        //调用互联网接口,来对用户输入的真实姓名和身份证号码进行匹配认证
        //String jsonString = (String) HttpClientUtils.doPost("https://way.jd.com/YOUYU365/keyelement",paramMap);
        String jsonString = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"serialNo\": \"5590601f953b512ff9695bc58ad49269\",\n" +
                "        \"respCode\": \"000000\",\n" +
                "        \"respMsg\": \"验证通过\",\n" +
                "        \"comfrom\": \"jd_query\",\n" +
                "        \"success\": \"false\"\n" +
                "    }\n" +
                "}";

        //将json格式的字符串转换为json对象
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //获取通信标识code
        String code = jsonObject.getString("code");
        //判断是否通信成功
        if (StringUtils.equals("10000", code)) {
            String success = jsonObject.getJSONObject("result").getString("success");

            if (StringUtils.equals(success, "true")) {
                retMap.put(Constants.ERROR_MESSAGE, "OK");
            } else {
                retMap.put(Constants.ERROR_MESSAGE, "false");
            }

        }
        return retMap;

    }
}
