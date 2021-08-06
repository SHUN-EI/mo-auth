package com.mo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mo on 2021/8/6
 */
@Data
@Builder
@TableName("tb_weixin")
public class WxInfo implements Serializable {

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
     * 用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的
     */
    private String unionid;
    /**
     * 普通用户昵称
     */
    private String nickname;
    /**
     * 普通用户性别，1为男性，2为女性
     */
    private String sex;
    /**
     * 普通用户个人资料填写的省份
     */
    private String province;
    /**
     * 普通用户个人资料填写的城市
     */
    private String city;
    /**
     * 国家，如中国为CN
     */
    private String country;
    /**
     * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
     */
    private String headimgurl;
    /**
     * 接口调用凭证
     */
    private String accessToken;
    /**
     * 获取凭证时间
     */
    private Date accessTokenDate;
    /**
     * 用户刷新access_token凭证
     */
    private String refreshToken;
    /**
     * 获取refresh_token时间
     */
    private Date refreshTokenDate;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 修改时间
     */
    private Date updateDate;

}
