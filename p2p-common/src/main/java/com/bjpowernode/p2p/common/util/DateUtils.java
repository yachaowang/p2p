package com.bjpowernode.p2p.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * ClassName:DateUtils
 * Package:com.bjpowernode.p2p.common.util
 * Description:
 *
 * @date:2019/4/1 11:01
 * @author:guoxin
 */
public class DateUtils {

    /**
     * 通过添加天数返回日期值
     * @param date
     * @param count
     * @return
     */
    public static Date getDateByAddDays(Date date, Integer count) {

        //创建日期处理类对象
        Calendar instance = Calendar.getInstance();

        //设置当前日期值
        instance.setTime(date);

        //添加天数
        instance.add(Calendar.DATE,count);


        return instance.getTime();
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(getDateByAddDays(new SimpleDateFormat("yyyy-MM-dd").parse("2008-08-08"),-2));
    }

    public static Date getDateByAddMonths(Date date, Integer count) {
        //创建日期处理类对象
        Calendar instance = Calendar.getInstance();

        //设置当前日期值
        instance.setTime(date);

        //添加月份
        instance.add(Calendar.MONDAY,count);


        return instance.getTime();
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyyMMddHHssSSS").format(new Date());
    }
}
