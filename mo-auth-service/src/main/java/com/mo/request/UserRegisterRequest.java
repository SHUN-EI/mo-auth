package com.mo.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.validate.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * Created by mo on 2021/7/22
 */
@ApiModel(value = "用户注册对象", description = "用户注册请求对象")
@Data
public class UserRegisterRequest {

    @JsonProperty("user_name")
    @ApiModelProperty(value = "用户账号", example = "zhangsan")
    @NotBlank(message = "用户账号不能为空", groups = {ValidUserName.class})
    @UserName(groups = ValidUserName.class)
    private String userName;

    //@JsonIgnore 的作用 转换为Json数据的时候，忽略密码
    //@JsonIgnore//在接受数据的时候，不能使用注解，否则数据会转换丢失
    @ApiModelProperty(value = "用户注册密码", example = "123456")
    //@NotBlank(message = "用户密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度最少6位，最多20位")
    private String password;

    @ApiModelProperty(value = "账户状态，1正常，0测试，-1禁用", example = "1")
    private Integer status;

    @ApiModelProperty(value = "用户手机号", example = "18866668888")
    @NotBlank(message = "手机号不能为空", groups = {ValidMobile.class})
    @Mobile(groups = {ValidMobile.class})
    private String mobile;

    //设置时间格式
    @ApiModelProperty(value = "绑定手机的时间", example = "2020-01-01 12:30:30")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date mobileBindDate;

    @ApiModelProperty(value = "绑定邮箱", example = "zhangsan@itcast.cn")
    @NotBlank(message = "邮箱不能为空", groups = {ValidEmail.class})
    @Email(groups = {ValidEmail.class})
    private String email;

    @ApiModelProperty(value = "绑定邮箱的时间", example = "2020-01-01 12:30:30")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date emailBindDate;

    @ApiModelProperty(value = "绑定QQ", example = "123321666")
    private String qq;

    @ApiModelProperty(value = "绑定QQ的时间", example = "2020-01-01 12:30:30")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date qqBindDate;

    @ApiModelProperty(value = "绑定微信唯一标识符", example = "123321666")
    private String weixin;

    @ApiModelProperty(value = "绑定微信的时间", example = "2020-01-01 12:30:30")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date weixinBindDate;

    @ApiModelProperty(value = "绑定新浪微博唯一标识符", example = "123321666")
    private String weibo;

    @ApiModelProperty(value = "绑定新浪微博的时间", example = "2020-01-01 12:30:30")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date weiboBindDate;

    @ApiModelProperty(value = "用户注册的时间", example = "2020-01-01 12:30:30")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "用户最后一次登录的时间", example = "2020-01-01 12:30:30")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastDate;

    @ApiModelProperty(value = "用户认证信息主键", example = "1001")
    private String authId;

    @ApiModelProperty(value = "用户所在经度", example = "116.333555")
    private String longitude;

    @ApiModelProperty(value = "用户所在纬度", example = "60.033555")
    private String latitude;

    @ApiModelProperty(value = "用户注册ip地址", example = "192.168.1.1")
    private String ip;

    @ApiModelProperty(value = "用户修改密码使用的旧密码")
    private String oldPassword;

    @ApiModelProperty(value = "图片验证码的key")
    @NotBlank(message = "用户账号key不能为空", groups = {ValidUserName.class})
    private String key;

    @ApiModelProperty(value = "验证码code")
    @NotBlank(message = "用户账号code不能为空", groups = {ValidUserName.class})
    private String code;
}
