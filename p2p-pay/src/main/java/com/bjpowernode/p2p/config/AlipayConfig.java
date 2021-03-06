package com.bjpowernode.p2p.config;

/**
 * @ClassName AlipayConfig
 * @Description
 * @Version 1.0
 * @Date 2019/4/2 20:52
 * @Author wyc
 **/
public class AlipayConfig {
    public static String gatewayUrl="https://openapi.alipaydev.com/gateway.do";
    public static String app_id="2016092700609322";
    public static String merchant_private_key="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDKpVuq/QKu+kqAy6QscHwzA9BqBlmAhe645VwW2JA5WH7uUpJUznYiU8Ztp1ZxNl2OQqp5Jk0iwdYLLgnqv4hFD8kvGWoGWPl++vSTRl+tlnQAqbg2ch95vldMVRPXVqO1XkAL1UB7MyFoE6WBtvwBg5kZcl73pjxMXve2tt6582RoLUztcBRzI6Ro7I/2en+JdD5kmlYMRRwJqXF6jFBb/UireZ0djF1Jfsq01CY0RyVYghSlrdN5J01VmbBssD/0l0RMpS58IqEr3mbxF8XvFuX45/6xL57xl3maA0/bObT6URmMW5cIcrj9OLnoQnBNwiWUdD6+q38uOQwEWImLAgMBAAECgf9vnKPgiXnw5eojBgZmc430iyU/QEALibjyHWGbM6w76YH/wXrXOt/WCE5CFuiFcmwU7qL2FyJlAl3hynmr0dI8N3WXe2fqFxdT2OTcOOZ5tWQu+Ut8sV6krzFl2XYUJKQGQOMFoW7ibEb6C9gcmawzKUgpu9SSkfykE6pb+Fy0DV3/9rRdUzWO2X4WcQUuN1kj449LWi+xYLn1U5IZCuovkm1tMnheqTxqKAiFls3H7PQsMayXdtedaG9Ro7tZFuo8tTq8NKK3VahfaWuUsQibCos70yGaakUTTt9aKjzowjWM0KDhi4FoPll5SD/cbmc0vQgTZBNyxo8XdUrY+ikCgYEA/KrXzTfDo2JVQ7CUXIUoMPtv8UuZzif4Lc4MS+VFzZG7Yls8iAbDwcpFgkvrxIOW9QSvyV8rBRvE3qPHtbVJjw6nSHeSSrAE6OVf23QnjdBVPnsZZ5+57a1DH7mPtJy9azm/vLFf5W9wSXWeMNBxcK/ZzSxCuve5fHw5h2Anc60CgYEAzVGc2BQH/uUf/PnTd05lUYn0gWrdKWX/6f3Jk5CH2YYyQuluSocc10Y6SP55Cd6RzhtE9IP9erQURW6CAC+OKCKJdn0ijpsmHnsFRoIuplUWt1UxySDO4zaSLRp7nOzKJOE0cWEF4rSy/aj0AKmk8cAlpP/v4KoqR+3hxXKuWRcCgYEAyATTC5FunaPVjd12xXtgIs6ZgrJvksUPcetgzqA0zxTj+2vwklqQk5P1zL0fapUlZtgL9OUS4FtoUTvkeFOIZOibwwFtXRTU/i2+4bKlMV3QbcLzjJXkkWQM9EtomPlP9ldkExNzr+S1RmIHFeudMjDzwbmdJfe/7e1FeYbEFI0CgYEAoEI9nGM2QnzxVPJjXtJwPw7hLEsPSYr2D9wqBOZhQyZ+AMBXeBCyME3+nI8URuHVCP07+2cy6R26XN9+1UpJOYaLqVLPMGRlgpEfcwOWCfqMepki/QyAr9Uej9oF9lzVA6hM2NqUT3of4+KGYiYIOzJxr7Kdt8zap75D5Ww4Lg0CgYEAyQ3XwrPTVWVxwQC126quCfkZFcH1WuLYm9lMiUCrsmyW5GnUG7CuzgYb4z9z95nWpxpXusKOVhTkyzNqg02nHIwQuyM+z8cOu/EhUdpoa21Rzueys3ya32amWA9JyUpdLC8IsiZJTJUNqeSzBBRd3p3ro/SxjBdC3admiGfWh3M=";
    public static String charset="UTF-8";

    public static String alipay_public_key="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArSc2cXAjExGbFT6j6E6yX3uP5/qdz5VKhibpLHcYc5bfZklDxP3HDtQQu+GgsvoOwJrilRFMxOmgc2Lj7MUz/1mDbgryLtfKlvIlBH50fQdTDSb+PYx343wF4wdVzsgTwAg6wfBn25uAFI4mlRD1q6jFKpBkwOsa0I+xL6o30xtxdzPQUf7ln9s1DwbqayrpmUD+lWlOdXXdJJqhFRXePR7dkwdwGF14jyVsQXXLxmIIdkyk2cn0L6PipQ2ksiNPAg3cUmzgerbYh4r5rz8rLK2mTyDNP2fS5eC4lOWApo/mcPVWfTWRcxJSVBiUqX+QTE9ez4/VKkpGUy88gyLoCwIDAQAB";
    public static String sign_type="RSA2";
    public static String return_url="http://localhost:9090/pay/api/alipayBack";
    public static String notify_url="http://localhost:9090/pay/api/alipayNotify";
}
