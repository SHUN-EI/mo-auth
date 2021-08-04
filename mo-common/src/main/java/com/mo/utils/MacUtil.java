package com.mo.utils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Created by mo on 2021/7/22
 * 消息摘要算法-MAC 加密工具类
 */
public class MacUtil {

    //声明用于生成密文的密钥，密钥可以自行修改
    private final static String USER_SECRET = "vNokTDRmbz&3YA!*";
    //设置加密算法
    private final static String HASH_TYPE = "HmacSHA256";

    public static String makeHashPassword(String Password) {
        SecretKey secretKey = new SecretKeySpec(USER_SECRET.getBytes(), HASH_TYPE);
        try {
            Mac mac = Mac.getInstance(HASH_TYPE);
            mac.init(secretKey);
            byte[] bytes = mac.doFinal(Password.getBytes());
            return new String(Base64.getEncoder().encode(bytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
