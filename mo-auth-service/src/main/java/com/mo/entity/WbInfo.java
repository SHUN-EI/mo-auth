package com.mo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mo on 2021/8/20
 */
@Data
@Builder
@TableName("tb_weibo")
public class WbInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户认证信息ID
     */
    @TableId(value = "auth_id", type = IdType.INPUT)
    private String authId;

    /**
     * 普通用户唯一标识
     */
    private String uid;

    /**
     * 用户昵称
     */
    private String screenName;

    /**
     * 用户所在地
     */
    private String location;

    /**
     * 用户头像地址（中图），50×50像素
     */
    private String profileImageUrl;

    /**
     * 用户头像地址（高清），高清头像原图
     */
    private String avatarHd;

    /**
     * 性别，m：男、f：女、n：未知
     */
    private String gender;

    /**
     * 用户当前的语言版本，zh-cn：简体中文，zh-tw：繁体中文，en：英语
     */
    private String lang;

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
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改时间
     */
    private Date updateDate;

}
