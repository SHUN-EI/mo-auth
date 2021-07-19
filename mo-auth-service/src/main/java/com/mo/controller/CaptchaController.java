package com.mo.controller;

import com.mo.model.Result;
import com.wf.captcha.ArithmeticCaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by mo on 2021/7/19
 */
@Api(tags = "验证码模块")
@RequestMapping("/api/captcha/v1")
@Slf4j
@RestController
public class CaptchaController {

    @ApiOperation("获取图形验证码")
    @GetMapping("/getCaptchaCode")
    public Result getCaptchaCode() {

        //生成验证码，创建验证码对象，使用算式验证码
        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(180, 80);
        //设置算式验证码的位数，几个数计算
        arithmeticCaptcha.setLen(3);
        //获取验证码结果
        String code = arithmeticCaptcha.text();
        //生成标识符，使用UUID作为key,key的作用是标识一个用户
        String key = UUID.randomUUID().toString();
        //封装返回结果数据
        Map map = new HashMap();
        map.put("key", key);
        //图片输出为base64编码
        map.put("captchaCode", arithmeticCaptcha.toBase64());
        //返回Result
        return Result.success("图片验证码创建成功", map);
    }

}
