<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>CSRF</title>
</head>
<body>
<h3>转账操作</h3>
<form action="/account/transfer" method="post">
    <!--直接输入用户即可转账,如 lisi-->
    <p>转账目标账户:<input type="text" name="account"></p>
    <input type="submit" value="转账">
</form>
<br/>

<h3>账户金额</h3>
<#list map?keys as key>
    ${key}:::${map[key]}<br/>
</#list>
</body>
</html>