package com.mo.captcha;

import com.mo.AuthApplication;
import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mo on 2021/7/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthApplication.class)
@Slf4j
public class CaptchaTest {

    @Test
    public void captchaTest() throws IOException, FontFormatException {

        String path = "/Users/mo/develop/workspace/mo-auth/mo-auth-service/src/main/resources/images/";

        //png格式-图片验证码
        SpecCaptcha specCaptcha = new SpecCaptcha(150, 50);
        FileOutputStream fos = new FileOutputStream(path + "0-png类型.png");
        //输出png图片验证码
        specCaptcha.out(fos);
        log.info("生成的png格式-图片验证码为:{}", specCaptcha.text());

        //gif动图-图片验证码
        GifCaptcha gifCaptcha = new GifCaptcha(150, 50);
        FileOutputStream fos2 = new FileOutputStream(path + "1-gif类型.gif");
        //设置纯大写字母验证码TYPE_ONLY_UPPER
        gifCaptcha.setCharType(Captcha.TYPE_ONLY_UPPER);
        //设置字体
        gifCaptcha.setFont(Captcha.FONT_5);
        //输出gif动图-图片验证码
        gifCaptcha.out(fos2);
        log.info("生成的gif动图-图片验证码为:{}", gifCaptcha.text());

        //中文类型-图片验证码
        ChineseCaptcha chineseCaptcha = new ChineseCaptcha(150, 50);
        FileOutputStream fos3 = new FileOutputStream(path + "2-中文类型.png");
        //输出中文类型-图片验证码
        chineseCaptcha.out(fos3);
        log.info("生成的中文类型-图片验证码为:{}", chineseCaptcha.text());

        //中文类型gif动图-图片验证码
        ChineseGifCaptcha chineseGifCaptcha = new ChineseGifCaptcha(150, 50);
        FileOutputStream fos4 = new FileOutputStream(path + "3-中文类型动图.gif");
        //输出中文类型gif动图-图片验证码
        chineseGifCaptcha.out(fos4);
        log.info("生成的中文类型gif动图-图片验证码为:{}", chineseGifCaptcha.text());

        //算术类型-图片验证码
        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(180, 80);
        FileOutputStream fos5 = new FileOutputStream(path + "4-算术类型.png");
        //设置几位数的运算，几个数计算
        arithmeticCaptcha.setLen(3);
        //输出算术类型-图片验证码
        arithmeticCaptcha.out(fos5);
        log.info("生成的算术类型-图片验证码的结果为:{}", arithmeticCaptcha.text());
        log.info("生成的算术类型-图片验证码的算式为:{}", arithmeticCaptcha.getArithmeticString());

    }
}
