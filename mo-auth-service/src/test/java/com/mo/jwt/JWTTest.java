package com.mo.jwt;

import com.mo.AuthApplication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Created by mo on 2021/7/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthApplication.class)
@Slf4j
public class JWTTest {

    //声明盐，不能泄露，存放在服务器
    private static String secret = "TQB^Ipw7KxMPj2nPx0FbRD%$M";

    @Test
    public void parserJWTTest() {
        String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJtby1hdXRoIn0.9_6Xmq_yg6q4d7g78rjm5FxqApUKirVo4iIVwxJ2eeA";

        //生成签名的key,使用java自带的Base64加密
        byte[] encodeKey = Base64.getEncoder().encode(secret.getBytes());
        SecretKey secretKey = new SecretKeySpec(encodeKey, 0, encodeKey.length, "AES");

        //解析jwt
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();

        log.info("jwt token的 id 为:{}", claims.getId());

    }

    @Test
    public void createJWTTest() {

        //生成签名的key,使用java自带的Base64加密
        byte[] encodeKey = Base64.getEncoder().encode(secret.getBytes());
        SecretKey secretKey = new SecretKeySpec(encodeKey, 0, encodeKey.length, "AES");

        //构建JWT
        JwtBuilder jwtBuilder = Jwts.builder()
                //{"typ":"JWT","alg":"HS256"}
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setId("mo-auth")//声明唯一标识jti
                .signWith(SignatureAlgorithm.HS256, secretKey); //设置密钥签名

        log.info("生成的jwt token为:{}", jwtBuilder.compact());
    }

}
