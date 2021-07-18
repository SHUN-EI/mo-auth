package com.mo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.validate.Mobile;
import com.mo.validate.UserName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.common.value.qual.MinLen;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * Created by mo on 2021/7/15
 */
@ApiModel(value = "用户认证信息对象", description = "用户认证信息请求对象")
@Data
public class AuthRequest {

    /**
     * 第几页
     */
    @Min(value = 1, message = "页码数从1开始")
    @JsonProperty("page_num")
    @ApiModelProperty(value = "第几页", example = "1")
    private Integer pageNum;
    /**
     * 每页显示多少条
     */
    @Min(value = 2, message = "每页最少显示5条数据")
    @Max(value = 30, message = "每页最多显示30条数据")
    @JsonProperty("page_size")
    @ApiModelProperty(value = "每页显示多少条", example = "2")
    private Integer pageSize;

    @ApiModelProperty(value = "用户认证信息主键ID", example = "10001")
    private String id;

    /**
     * 用户账号
     */
    @UserName
    @JsonProperty("user_name")
    @ApiModelProperty(value = "用户账号", example = "zhangsan")
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 账户状态，1正常，0测试，-1禁用
     */
    @Range(min = -1, max = 1, message = "用户状态数据错误")
    @ApiModelProperty(value = "账户状态，1正常，0测试，-1禁用", example = "1")
    private Integer status;


    /**
     * 用户手机号
     */
    @Mobile
    @ApiModelProperty(value = "用户手机号", example = "18866668888")
    private String mobile;

    /**
     * 绑定手机的时间
     */
    @JsonProperty("mobile_bind_date")
    @ApiModelProperty(value = "绑定手机的时间", example = "2020-01-01 12:30:30")
    private Date mobileBindDate;

    /**
     * 绑定邮箱
     */
    @Email
    @ApiModelProperty(value = "绑定邮箱", example = "zhangsan@itcast.cn")
    private String email;

    /**
     * 绑定邮箱的时间
     */
    @JsonProperty("email_bind_date")
    @ApiModelProperty(value = "绑定邮箱的时间", example = "2020-01-01 12:30:30")
    private Date emailBindDate;

    /**
     * 绑定QQ
     */
    @ApiModelProperty(value = "绑定QQ", example = "123321666")
    private String qq;

    /**
     * 绑定QQ的时间
     */
    @JsonProperty("qq_bind_date")
    @ApiModelProperty(value = "绑定QQ的时间", example = "2020-01-01 12:30:30")
    private Date qqBindDate;

    /**
     * 绑定微信唯一标识符
     */
    @ApiModelProperty(value = "绑定微信唯一标识符", example = "123321666")
    private String weixin;

    /**
     * 绑定微信的时间
     */
    @ApiModelProperty(value = "绑定微信的时间", example = "2020-01-01 12:30:30")
    @JsonProperty("weixin_bind_date")
    private Date weixinBindDate;

    /**
     * 绑定新浪微博唯一标识符
     */
    @ApiModelProperty(value = "绑定新浪微博唯一标识符", example = "123321666")
    private String weibo;

    /**
     * 绑定新浪微博的时间
     */
    @JsonProperty("weibo_bind_date")
    @ApiModelProperty(value = "绑定新浪微博的时间", example = "2020-01-01 12:30:30")
    private Date weiboBindDate;

    /**
     * 用户注册的时间
     */
    @JsonProperty("create_date")
    @ApiModelProperty(value = "用户注册的时间", example = "2020-01-01 12:30:30")
    private Date createDate;

    /**
     * 用户最后一次登录的时间
     */
    @JsonProperty("last_date")
    @ApiModelProperty(value = "用户最后一次登录的时间", example = "2020-01-01 12:30:30")
    private Date lastDate;
}
