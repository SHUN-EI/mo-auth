package com.mo.constant;

/**
 * Created by mo on 2021/7/20
 */
public class CacheKey {


    //手机验证码发送间隔时间，1分钟
    public static final long CAPTCHAINTERVAL = 60;
    //手机验证码24小时内发送的次数
    public static final long CAPTCHACOUNT = 6;
    //验证码有效时间，5分钟
    public static final long CAPTCHAEXPIRE = 5 * 60;
    //redis的保存token的有效时间为token的有效时间
    public static final long TOKENEXPIRETIME = 3 * 24 * 60 * 60;

    //维护redis中验证码的key的前缀
    //图片验证码
    private static final String CAPTCHA_IMAGE = "CAPTCHA_IMAGE_";

    //邮箱验证码
    private static final String CAPTCHA_EMAIL = "CAPTCHA_EMAIL_";

    //手机验证码
    private static final String CAPTCHA_MOBILE = "CAPTCHA_MOBILE_";

    //同一个手机验证码发送的次数
    private static final String CAPTCHA_COUNT = "CAPTCHA_COUNT_";

    //鉴权签发的token前缀
    private static final String JWT_TOKEN = "JWT_TOKEN_";

    /**
     * 注册验证码，第一个%s是验证码类型，第二个%s是接收号码
     */
    public static final String CHECK_CODE_KEY = "code:%s:%s";

    public static String getCaptchaImage(String image) {
        return CAPTCHA_IMAGE + image;
    }

    public static String getCaptchaEmail(String email) {
        return CAPTCHA_EMAIL + email;
    }

    public static String getCaptchaMobile(String mobile) {
        return CAPTCHA_MOBILE + mobile;
    }

    public static String getCAPTCHACOUNT(String mobile) {
        return CAPTCHA_COUNT + mobile;
    }

    public static String getJwtToken(String authId) {
        return JWT_TOKEN + authId;
    }
}
