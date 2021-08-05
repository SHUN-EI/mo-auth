package com.mo.enums;

import lombok.Getter;

/**
 * Created by mo on 2021/7/26
 * 登录类型
 */
@Getter
public enum AuthType {

    //未知类型
    UNKNOWN(0),
    //手机号
    MOBILE(1),
    //电子邮箱
    EMAIL(2),
    //账户名
    USERNAME(3),

    //微信
    WEIXIN(4),
    //微博
    WEIBO(5),
    //QQ
    QQ(6);

    private int type;

    AuthType(int type) {
        this.type = type;
    }

    //根据type类型，可以获取到对应的枚举
    public static AuthType getAuthType(int type) {
        for (AuthType authType : AuthType.values()) {
            if (authType.getType() == type) {
                return authType;
            }
        }

        return UNKNOWN;
    }

}
