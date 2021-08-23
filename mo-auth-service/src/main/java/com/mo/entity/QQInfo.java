package com.mo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mo on 2021/8/23
 */
@Data
@Builder
@TableName("tb_qq")
public class QQInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户认证信息ID
     */
    @TableId(value = "auth_id", type = IdType.INPUT)
    private String authId;

    /**
     * 普通用户的标识，对当前开发者帐号唯一
     */
    private String openid;

    /**
     * 用户在QQ空间的昵称
     */
    private String nickname;

    /**
     * 性别。 如果获取不到则默认返回男
     */
    private String gender;

    /**
     * 所在的省份
     */
    private String province;

    /**
     * 所在的城市
     */
    private String city;

    /**
     * 大小为40×40像素的QQ头像
     */
    private String figureurl_qq_1;

    /**
     * 大小为100×100的QQ头像。用户不一定拥有100x100QQ头像，但40x40一定会有
     */
    private String figureurl_qq_2;

    /**
     * 接口调用凭证
     */
    private String accessToken;

    /**
     * 调用凭证获取时间
     */
    private Date accessTokenDate;

    /**
     * 调用凭证过期时间
     */
    private int expiresIn;

    /**
     * 用于刷新access_token，且仅一次有效
     */
    private String refreshToken;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改时间
     */
    private Date updateDate;
}
