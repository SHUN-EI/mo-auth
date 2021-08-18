package com.mo.controller;

import com.mo.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mo on 2021/8/13
 */
@Controller
@RequestMapping("/user")
public class UserController {

    //模拟数据库存储
    private ConcurrentHashMap map = new ConcurrentHashMap();

    @RequestMapping("/json")
    @ResponseBody
    public Map saveJson(@RequestBody User user) {
        System.out.println("UserController saveJson:user:" + user);

        map.put(user.getName(), user.getAge());

        return map;
    }


    @RequestMapping("/save")
    public String save(User user, Model model) {
        System.out.println("UserController save...." + user);

        //模拟保存数据
        map.put(user.getName(), user.getAge());

        //返回数据
        model.addAttribute("map", map);

        return "xssDemo";
    }

}
