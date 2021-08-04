package com.mo.controller;

import com.mo.constant.CacheKey;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.SendCodeEnum;
import com.mo.model.Result;
import com.mo.service.NotifyService;
import com.mo.utils.CommonUtil;
import com.mo.utils.RedisUtil;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by mo on 2021/7/20
 */
@Api(tags = "通知模块")
@RequestMapping("/api/notify/v1")
@Slf4j
@RestController
public class NotifyController {

    @Autowired
    private NotifyService notifyService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 发送验证码
     * 1.匹配图形验证码是否正常
     * 2.发送验证码
     *
     * @return
     */
    @ApiOperation("发送验证码")
    @GetMapping("/sendCode")
    public Result sendCode(@RequestParam(value = "to", required = true) String to,
                           @RequestParam(value = "captcha", required = true) String captcha,
                           HttpServletRequest request) {

        String captchaKey = getCaptchaKey(request);
        //获取redis缓存的图形验证码
        String cacheCaptcha = redisUtil.get(captchaKey);

        if (captcha != null && cacheCaptcha != null && captcha.equalsIgnoreCase(cacheCaptcha)) {

            //删除缓存中的验证码,不删除的话就过期时间自动删除
            redisUtil.del(captchaKey);
            //发送邮箱验证码
            Result result = notifyService.sendCode(SendCodeEnum.USER_REGISTER, to);
            return result;

        } else {
            return Result.buildResult(BizCodeEnum.CODE_CAPTCHA_ERROR);
        }
    }

    /**
     * 获取图形验证码
     */
    @ApiOperation("获取图形验证码-图像")
    @GetMapping("/getCaptchaCodeImage")
    public void getCaptchaCodeImage(HttpServletRequest request, HttpServletResponse response) {

//        //这里使用算式验证码
//        //生成验证码，创建验证码对象，使用算式验证码
//        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(180, 80);
//        //设置算式验证码的位数，几个数计算
//        arithmeticCaptcha.setLen(3);
//        //获取验证码结果
//        String code = arithmeticCaptcha.text();


        try (ServletOutputStream outputStream = response.getOutputStream()) {

            //缓存的key,key的作用是标识一个用户
            String key = getCaptchaKey(request);

            //gif动图-图片验证码
            GifCaptcha gifCaptcha = new GifCaptcha(150, 50);
            //设置字体
            gifCaptcha.setFont(Captcha.FONT_5);

            //把验证码信息保存到缓存中,还需要设置有效时间
            redisUtil.set(key, gifCaptcha.text(), CacheKey.CAPTCHAEXPIRE);

            response.setContentType("image/gif");
            //图形验证码输出
            gifCaptcha.out(outputStream);
            log.info("生成的gif动图-图片验证码为:{}", gifCaptcha.text());
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            log.error("获取图形验证码异常:{}", e);
        }


    }

    @ApiOperation("获取图形验证码")
    @GetMapping("/getCaptchaCode")
    public Result getCaptchaCode(HttpServletRequest request) throws Exception {

//        //生成验证码，创建验证码对象，使用算式验证码
//        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(180, 80);
//        //设置算式验证码的位数，几个数计算
//        arithmeticCaptcha.setLen(3);
//        //获取验证码结果
//        String code = arithmeticCaptcha.text();

        //缓存的key,key的作用是标识一个用户
        String key = getCaptchaKey(request);

        //gif动图-图片验证码
        GifCaptcha gifCaptcha = new GifCaptcha(150, 50);
        //设置字体
        gifCaptcha.setFont(Captcha.FONT_5);
        String code = gifCaptcha.text();

        //把验证码信息保存到缓存中,还需要设置有效时间
        redisUtil.set(key, code, CacheKey.CAPTCHAEXPIRE);

        //日志输出
        log.info("生成的验证码图片：key={},code={} ", key, code);

        //封装返回结果数据
        Map map = new HashMap();
        map.put("key", key);
        map.put("code", code);
        //图片输出为base64编码
        map.put("gifCaptcha", gifCaptcha.toBase64());
        //返回Result
        return Result.success("图片验证码创建成功", map);
    }

    /**
     * 获取图形验证码的缓存的key
     *
     * @param request
     */
    private String getCaptchaKey(HttpServletRequest request) {

        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        //把ip和userAgent 拼接进行MD5加密
        String secret = CommonUtil.MD5(ip + userAgent);
        String key = CacheKey.getCaptchaImage(secret);

        log.info("ip={}", ip);
        log.info("UserAgent={}", userAgent);
        log.info("key={}", key);
        return key;
    }

}
