package com.mo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by mo on 2021/8/6
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wx")
public class WXConfig {

    /**
     * 微信的appid,应用唯一标识，在微信开放平台提交应用审核通过后获得
     */
    private String appid;

    /**
     * 微信的appsecret,应用密钥AppSecret，在微信开放平台提交应用审核通过后获得
     */
    private String secret;

    /**
     * 请求 access_token 接口地址
     */
    private String accessTokenUrl;

    /**
     * 刷新access_token 接口地址
     */
    private String accessRefreshUrl;

    /**
     * 微信用户信息接口地址
     */
    private String wxInfoUrl;
}
