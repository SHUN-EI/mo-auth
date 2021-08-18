<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>CSRF</title>
</head>
<body>
<h3>用户登录</h3>
<form action="/account/login" method="post">
    <p>账户:<input type="text" name="username"></p>
    <p>密码:<input type="text" name="password"></p>
    <input type="submit" value="提交">
</form>
</body>
</html>