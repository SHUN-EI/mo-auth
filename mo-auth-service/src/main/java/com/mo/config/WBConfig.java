package com.mo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mo on 2021/8/20
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wb")
public class WBConfig {
    /**
     * 微博的appid,应用唯一标识
     */
    private String appid;
    /**
     * 微博的appsecret,应用密钥AppSecret
     */
    private String secret;
    /**
     * 授权回调地址，回调地址，需与注册应用里的回调地址一致
     */
    private String redirectUri;
    /**
     * 认证地址
     */
    private String authorizeUrl;
    /**
     * 请求微博 access_token 接口地址
     */
    private String accessTokenUrl;
    /**
     * 微博用户信息接口地址
     */
    private String wbInfoUrl;
}
