package com.bjpowernode.p2p.config;

/**
 * @ClassName WXPayConfig
 * @Description
 * @Version 1.0
 * @Date 2019/4/3 20:05
 * @Author wyc
 **/
public class WXPayConfig {
    public static String appid = "wx8a3fcf509313fd74";
    public static String mch_id = "1361137902";
    public static String spbill_create_ip = "127.0.0.1";
    public static String notify_url = "http://localhost:9090/pay/api/wxpayNotify";
    public static String trade_type = "NATIVE";
    public static String key = "367151c5fd0d50f1e34a68a802d6bbca";
    public static String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

}
