<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>XSS演示</title>
</head>
<body>
<h3>提交用户数据</h3>
<form action="/user/save" method="post">
    <p>name:<input type="text" name="name" size="35"></p>
    <p>age:<input type="text" name="age" size="37"></p>
    <input type="submit" value="提交">
</form>

<h3>展示用户列表：</h3>
<#list  map?keys as key>
${key}:::${map[key]} <br/>
</#list>
</body>
</html>