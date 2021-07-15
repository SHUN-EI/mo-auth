package com.mo.request;

import lombok.Data;

import java.util.Date;

/**
 * Created by mo on 2021/7/15
 */
@Data
public class AuthListRequest {

    /**
     * 第几页
     */
    private Integer pageNum;
    /**
     * 每页显示多少条
     */
    private Integer pageSize;

    private String id;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 密码
     */
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
    private Date mobileBindDate;

    /**
     * 绑定邮箱
     */
    private String email;

    /**
     * 绑定邮箱的时间
     */
    private Date emailBindDate;

    /**
     * 绑定QQ
     */
    private String qq;

    /**
     * 绑定QQ的时间
     */
    private Date qqBindDate;

    /**
     * 绑定微信唯一标识符
     */
    private String weixin;

    /**
     * 绑定微信的时间
     */
    private Date weixinBindDate;

    /**
     * 绑定新浪微博唯一标识符
     */
    private String weibo;

    /**
     * 绑定新浪微博的时间
     */
    private Date weiboBindDate;

    /**
     * 用户注册的时间
     */
    private Date createDate;

    /**
     * 用户最后一次登录的时间
     */
    private Date lastDate;
}
