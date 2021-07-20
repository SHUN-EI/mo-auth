package com.mo.constant;

/**
 * Created by mo on 2021/7/20
 */
public class CacheKey {


    //手机验证码发送间隔时间，1分钟
    public static long captchaInterval = 60;
    //手机验证码24小时内发送的次数
    public static long captchaCount = 6;
    //验证码有效时间，5分钟
    public static long captchaExpire = 300;

    //维护redis中验证码的key的前缀
    //图片验证码
    private final static String CAPTCHA_IMAGE = "CAPTCHA_IMAGE_";

    //邮箱验证码
    private final static String CAPTCHA_EMAIL = "CAPTCHA_EMAIL_";

    //手机验证码
    private final static String CAPTCHA_MOBILE = "CAPTCHA_MOBILE_";

    //同一个手机验证码发送的次数
    private final static String CAPTCHA_COUNT = "CAPTCHA_COUNT_";

    //鉴权签发的token前缀
    private final static String JWT_TOKEN = "JWT_TOKEN_";

    public static String getCaptchaImage(String image) {
        return CAPTCHA_IMAGE + image;
    }

    public static String getCaptchaEmail(String email) {
        return CAPTCHA_EMAIL + email;
    }

    public static String getCaptchaMobile(String mobile) {
        return CAPTCHA_MOBILE + mobile;
    }

    public static String getCaptchaCount(String mobile) {
        return CAPTCHA_COUNT + mobile;
    }

    public static String getJwtToken(String authId) {
        return JWT_TOKEN + authId;
    }
}
