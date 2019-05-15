package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.http.HttpClientUtils;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.common.util.DateUtils;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.bjpowernode.p2p.service.loan.RedisService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RechargeRecordController
 * @Description
 * @Version 1.0
 * @Date 2019/4/2 17:40
 * @Author wyc
 **/
@Controller
public class RechargeRecordController {
    @Autowired
    private RedisService redisService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @RequestMapping(value = "/loan/alipayToRecharge")
    public String alipayRecharge(HttpServletRequest request, Model model,
                                 @RequestParam(value = "rechargeMoney", required = true) Double rechargeMoney) {
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        //生成全局唯一的充值订单号 = 时间戳 + redis全局唯一数字
        String rechargeNo = DateUtils.getTimestamp() + redisService.getOnlyNumber();
        //生成充值记录
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(sessionUser.getId());
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeDesc("支付宝充值");

        int addRechargeCount = rechargeRecordService.addRechargeRecord(rechargeRecord);
        if (addRechargeCount > 0) {

            model.addAttribute("rechargeNo", rechargeNo);
            model.addAttribute("rechargeMoney", rechargeMoney);
            model.addAttribute("rechargeDesc", "支付宝充值");
        } else {
            model.addAttribute("trade_msg", "充值人数过多,请稍后重试...");
            return "toRechargeBack";
        }
        return "p2pToPayAlipay";
    }


    @RequestMapping(value = "/loan/alipayBack")
    public String alipayBack(HttpServletRequest request, Model model,
                             @RequestParam(value = "out_trade_no", required = true) String out_trade_no,
                             @RequestParam(value = "total_amount", required = true) Double total_amount) throws Exception {

        //准备参数
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("out_trade_no", out_trade_no);

        //调用pay工程的订单查询接口,获取订单的处理结果
        String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/alipayQuery", paramMap);

        //使用fastjson解析json格式的字符串
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //获取alipay_trade_query_response所对应的json对象
        JSONObject tradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");

        //获取通信标识code
        String code = tradeQueryResponse.getString("code");
        if (StringUtils.equals("10000", code)) {
            //获取业务处理结果trade_status
            String tradeStatus = tradeQueryResponse.getString("trade_status");
            System.out.println("tradeStatus===" + tradeStatus);
            if (StringUtils.equals(tradeStatus, "TRADE_CLOSED")) {
                RechargeRecord rechargeRecord = new RechargeRecord();
                rechargeRecord.setRechargeNo(out_trade_no);
                rechargeRecord.setRechargeStatus("2");

                int modifyRechargeCount = rechargeRecordService.modifyRechargeRecordByRechargeNo(rechargeRecord);
                model.addAttribute("trade_msg", "充值失败,请稍后重试...");
                return "toRechargeBack";
            }
            //如果状态为TRADE_SUCCESS,给用户充值
            if (StringUtils.equals(tradeStatus, "TRADE_SUCCESS")) {
                //从session中获取用户的信息
                User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

                paramMap.put("uid", sessionUser.getId());
                paramMap.put("rechargeMoney", total_amount);
                paramMap.put("rechargeNo", out_trade_no);

                int rechargeCount = rechargeRecordService.recharge(paramMap);
                if (rechargeCount <= 0) {
                    model.addAttribute("trade_msg", "充值异常,请稍后重试...");
                    return "toRechargeBack";
                }
            }
        } else {
            model.addAttribute("trade_msg", "通信异常,请稍后重试...");
            return "toRechargeBack";
        }
        return "redirect:/loan/myCenter";

    }

    @RequestMapping(value = "/loan/wxpayToRecharge")
    public String wxpayRecharge(HttpServletRequest request, Model model,
                                @RequestParam(value = "rechargeMoney", required = true) Double rechargeMoney) {
        System.out.println("--------wxpay------");
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        //生成全局唯一的充值订单号 = 时间戳 + redis全局唯一数字
        String rechargeNo = DateUtils.getTimestamp() + redisService.getOnlyNumber();
        //生成充值记录订单
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(sessionUser.getId());
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeDesc("微信充值");

        int addRechargeCount = rechargeRecordService.addRechargeRecord(rechargeRecord);
        if (addRechargeCount > 0) {
            model.addAttribute("rechargeNo", rechargeNo);
            model.addAttribute("rechargeMoney", rechargeMoney);
            model.addAttribute("rechargeTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } else {
            model.addAttribute("trade_msg", "充值异常,请稍后重试...");
            return "toRechargeBack";
        }
        return "showQRCode";

    }

    @RequestMapping(value = "/loan/generateQRCode")
    public void generateQRCode(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam (value = "rechargeNo",required = true) String rechargeNo,
                               @RequestParam (value = "rechargeMoney",required = true) Double rechargeMoney) throws Exception {
        //准备wxpay统一下单API接口需要的参数
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("out_trade_no",rechargeNo);
        paramMap.put("total_fee",rechargeMoney);
        paramMap.put("body","微信扫码支付");

        //调用pay工程的统一下单API接口,包含code_url
        String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/wxpay",paramMap);
        //将json格式的字符串转换为json对象
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //获取通信标识return_code
        String returnCode = jsonObject.getString("return_code");

        if(StringUtils.equals(Constants.SUCCESS,returnCode)){
            //获取业务处理结果result_code
            String resultCode = jsonObject.getString("result_code");
            //判断业务处理结果
            if(StringUtils.equals(Constants.SUCCESS,resultCode)){
                //获取code_url
                String codeUrl = jsonObject.getString("code_url");
                //生成二维码
                Map<EncodeHintType,Object> encodeHintTypeObjectMap = new HashMap<EncodeHintType, Object>();
                encodeHintTypeObjectMap.put(EncodeHintType.CHARACTER_SET,"UTF-8");
                BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl, BarcodeFormat.QR_CODE,200,200,encodeHintTypeObjectMap);

                OutputStream outputStream = response.getOutputStream();
                //将矩阵对象转换为二维码
                MatrixToImageWriter.writeToStream(bitMatrix,"png",outputStream);
                outputStream.flush();
                outputStream.close();
            }else {
                response.sendRedirect(request.getContextPath() + "/toRechargeBack.jsp");
            }
        }else {
            response.sendRedirect(request.getContextPath() + "/toRechargeBack.jsp");
        }

    }
}
