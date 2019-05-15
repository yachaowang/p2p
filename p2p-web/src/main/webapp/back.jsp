<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script language="javascript" type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<script language="javascript" type="text/javascript" src="${pageContext.request.contextPath}/js/realName.js"></script>
<html>
<head>
    <title>银行卡验证</title>
</head>
<body>
<div>
    <label>姓名</label>
    <input type="text" id="name" name="name" maxlength="20"/>
</div>
<div>
    <label>身份证</label>
    <input type="text" id="idCard" name="idCard" maxlength="20"/>
</div>
<div>
    <label>手机</label>
    <input type="text" id="phone" name="phone" maxlength="20"/>
</div>
<div>
    <label>银行卡号</label>
    <input type="text" id="backId" name="backId" maxlength="20"/>
</div>
<button onclick="back();">认&nbsp;&nbsp;证</button>
</body>
</html>
