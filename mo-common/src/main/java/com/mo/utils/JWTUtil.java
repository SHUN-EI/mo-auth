package com.mo.utils;

import com.mo.enums.BizCodeEnum;
import com.mo.exception.BizException;
import com.mo.model.VerifyResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

/**
 * Created by mo on 2021/8/2
 */
public class JWTUtil {

    /**
     * 令牌前缀
     */
    private static final String TOKEN_PREFIX = "moauth";

    /**
     * subject,颁布者
     */
    private static final String SUBJECT = "waynemo";

    /**
     * 加密的密钥
     */
    private static final String SECRET = "moauthsecret";

    /**
     * 根据用户信息，生成token令牌
     *
     * @param authId
     * @return
     */
    public static String generateJsonWebToken(String authId) {

        if (null == authId) {
            throw new BizException(BizCodeEnum.ACCOUNT_UNREGISTER);
        }

        //获取secretKey
        SecretKey key = createKey();

        //系统默认时区
        ZoneId zoneId = ZoneId.systemDefault();
        //获取签发时间
        ZonedDateTime zdt = LocalDateTime.now().atZone(zoneId);
        //获取失效时间,设置失效时间是3天后
        //为了提高用户的体验，设置失效时间是凌晨3点,获取3天后的整点，再加3个小时
        ZonedDateTime zdtEnd = zdt.truncatedTo(ChronoUnit.DAYS).plusDays(3).plusHours(3);

        //构建JWT token
        String token = Jwts.builder()
                .setSubject(SUBJECT)
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setIssuedAt(Date.from(zdt.toInstant()))//签发时间
                .setExpiration(Date.from(zdtEnd.toInstant()))//过期时间
                .claim("authId", authId)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();


        return TOKEN_PREFIX + token;
    }

    /**
     * 校验token
     *
     * @param token
     * @return
     */
    public static VerifyResult checkJWT(String token) {
        //获取secretKey
        SecretKey key = createKey();
        try {
            //解析jwt
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();

            //获取认证信息
            String authId = (String) claims.get("authId");

            //返回结果
            return new VerifyResult(true, "校验成功", authId);

        } catch (ExpiredJwtException e) {
            //异常处理,jwt过期
            return new VerifyResult(false, "token过期", null);
        } catch (Exception e) {
            //其他异常处理，jwt无效
            return new VerifyResult(false, "token无效", null);
        }

    }

    /**
     * 生成secretKey
     *
     * @return
     */
    private static SecretKey createKey() {
        byte[] encodeKey = Base64.getEncoder().encode(SECRET.getBytes());
        SecretKey key = new SecretKeySpec(encodeKey, 0, encodeKey.length, "AES");
        return key;
    }
}
