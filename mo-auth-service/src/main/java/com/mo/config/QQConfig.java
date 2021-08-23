package com.mo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mo on 2021/8/23
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "qq")
public class QQConfig {
    /**
     * QQ的appid,应用唯一标识
     */
    private String appid;
    /**
     * QQ的appKey,应用密钥appKey
     */
    private String appKey;
    /**
     * QQ 回调地址
     */
    private String redirectUri;
    /**
     * QQ accessToken 获取地址
     */
    private String accessTokenUrl;
    /**
     * QQ openId获取地址
     */
    private String openIdUrl;
    /**
     * QQ用户信息接口地址
     */
    private String qqInfoUrl;
}
