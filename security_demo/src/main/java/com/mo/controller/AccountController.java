package com.mo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mo on 2021/8/18
 */
@Controller
@RequestMapping("/account")
public class AccountController {

    //使用Map当作数据库
    private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap();

    //初始化账户
    {
        map.put("zhangsan", 1000);
        map.put("lisi", 1000);
        map.put("wangwu", 1000);
    }

    /**
     * 使用session的方式登录
     *
     * @param username
     * @param password
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/login")
    public String login(String username, String password, HttpSession session, Model model) {
        if ("zhangsan".equals(username) && "123456".equals(password)) {
            session.setAttribute("username", "zhangsan");
        } else {
            return "csrf_login";
        }

        model.addAttribute("map", map);
        return "csrf_demo";
    }

    /**
     * 转账功能
     */
    @RequestMapping("/transfer")
    public String transfer(String account, HttpSession session, Model model) {
        //判断是否为张三登录
        if ("zhangsan".equals(session.getAttribute("username"))) {
            //判断转账账户是否可用
            if (account != null && !"".equals(account)) {
                //转账操作
                map.put("zhangsan", map.get("zhangsan") - 100);
                map.put(account, map.get(account) + 100);
            }
        } else {
            return "csrf_login";
        }
        //传递数据到前台页面
        model.addAttribute("map", map);
        return "csrf_demo";
    }

}
