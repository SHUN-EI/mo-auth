package com.mo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by mo on 2021/7/15
 */
@Data
public class AuthDTO {

    /**
     * 用户认证信息主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 用户账号
     */
    @JsonProperty("user_name")
    private String userName;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 账户状态，1正常，0测试，-1禁用
     */
    private Integer status;


    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 绑定手机的时间
     */
    @JsonProperty("mobile_bind_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date mobileBindDate;

    /**
     * 绑定邮箱
     */
    private String email;

    /**
     * 绑定邮箱的时间
     */
    @JsonProperty("email_bind_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date emailBindDate;

    /**
     * 绑定QQ
     */
    private String qq;

    /**
     * 绑定QQ的时间
     */
    @JsonProperty("qq_bind_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date qqBindDate;

    /**
     * 绑定微信唯一标识符
     */
    private String weixin;

    /**
     * 绑定微信的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("weixin_bind_date")
    private Date weixinBindDate;

    /**
     * 绑定新浪微博唯一标识符
     */
    private String weibo;

    /**
     * 绑定新浪微博的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("weibo_bind_date")
    private Date weiboBindDate;

    /**
     * 用户注册的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("create_date")
    private Date createDate;

    /**
     * 用户最后一次登录的时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("last_date")
    private Date lastDate;
}
