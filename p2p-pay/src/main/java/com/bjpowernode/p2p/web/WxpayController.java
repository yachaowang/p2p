package com.bjpowernode.p2p.web;

import com.bjpowernode.http.HttpClientUtils;
import com.bjpowernode.p2p.config.WXPayConfig;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WxpayController
 * @Description
 * @Version 1.0
 * @Date 2019/4/3 19:51
 * @Author wyc
 **/
@Controller
public class WxpayController {

    @RequestMapping(value = "/api/wxpay")
    @ResponseBody
    public Object generateQRCode(HttpServletRequest request,
                                 @RequestParam(value = "body",required = true) String body,
                                 @RequestParam (value = "out_trade_no",required = true) String out_trade_no,
                                 @RequestParam (value = "total_fee",required = true) String total_fee) throws Exception {

        //创建一个请求参数的Map集合
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid", WXPayConfig.appid);
        paramMap.put("mch_id",WXPayConfig.mch_id);
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
        paramMap.put("body",body);
        paramMap.put("out_trade_no",out_trade_no);
        BigDecimal bigDecimal = new BigDecimal(total_fee);
        BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
        int intValue = multiply.intValue();
        paramMap.put("total_fee",String.valueOf(intValue));
        paramMap.put("spbill_create_ip",WXPayConfig.spbill_create_ip);
        paramMap.put("notify_url",WXPayConfig.notify_url);
        paramMap.put("trade_type",WXPayConfig.trade_type);
        paramMap.put("product_id",out_trade_no);

        //生成签名值
        String signature = WXPayUtil.generateSignature(paramMap, WXPayConfig.key);
        paramMap.put("sign",signature);
        //将map集合的请求参数转换为xml格式的请求参数
        String requestDataXML = WXPayUtil.mapToXml(paramMap);
        //调用微信支付的统一下单API接口,返回的是xml格式的字符串
        String responseDataXml = HttpClientUtils.doPostByXml(WXPayConfig.url,requestDataXML);
        //将响应的xml格式的字符串转换为Map集合
        Map<String,String> responseDataMap = WXPayUtil.xmlToMap(responseDataXml);
        return responseDataMap;
    }
}
