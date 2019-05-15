package com.bjpowernode.p2p.service.loan;

public interface RedisService {
    /**
     * 将值存放到指定的redis中的key
     * @param key
     * @param value
     */
    void put(String key, String value);

    /**
     *从redis中获取key对应的值
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 生成redis全局唯一数字
     * @return
     */
    Long getOnlyNumber();
}
