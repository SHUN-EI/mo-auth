package com.mo.controller;

import com.mo.feign.AuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by mo on 2021/8/19
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AuthClient authClient;

    //不需要鉴权的接口测试
    @GetMapping("/noVerify")
    public String noVerify(@RequestHeader(required = false) String authId) {
        return "不需要鉴权的接口,获取到的用户ID：" + authId;

    }

    //需要鉴权的接口测试
    @GetMapping("/Verify")
    public String verify(@RequestHeader(required = true) String authId) {
        //Map map = authClient.queryWxInfo("1253553358925529089");

        return "需要鉴权的接口,获取到的用户微信信息：" + authId;

    }
}
