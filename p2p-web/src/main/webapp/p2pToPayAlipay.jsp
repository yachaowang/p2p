<%--
  Created by IntelliJ IDEA.
  User: 49740
  Date: 2019/4/2
  Time: 20:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>p2p调用pay工程的alipay方法</title>
</head>
<body>
<form method="post" action="http://localhost:9090/pay/api/alipay">
    <input type="hidden" name="out_trade_no" value="${rechargeNo}"/>
    <input type="hidden" name="total_amount" value="${rechargeMoney}"/>
    <input type="hidden" name="subject" value="${rechargeDesc}"/>
</form>
<script>document.forms[0].submit();</script>
</body>
</html>
