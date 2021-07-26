package com.mo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.validate.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Created by mo on 2021/7/26
 */
@ApiModel(value = "用户登录对象", description = "用户登录请求对象")
@Data
public class UserLoginRequest {

    @ApiModelProperty(value = "任意登录信息", example = "zhangsan")
    private String input;

    @JsonProperty("user_name")
    @ApiModelProperty(value = "用户账号", example = "zhangsan")
    @NotBlank(message = "用户账号不能为空", groups = {ValidUserName.class})
    @UserName(groups = ValidUserName.class)
    private String userName;

    @ApiModelProperty(value = "用户密码")
    @NotBlank(message = "用户密码不能为空")
    //@Length(min = 6, max = 20, message = "密码长度最少6位，最多20位")
    private String password;

    @ApiModelProperty(value = "图片验证码的key")
    private String key;

    @ApiModelProperty(value = "验证码")
    private String code;

    @ApiModelProperty(value = "手机号", example = "18811112222")
    @NotBlank(message = "手机号不能为空", groups = {ValidMobile.class})
    @Mobile(groups = {ValidMobile.class})
    private String mobile;

    @ApiModelProperty(value = "邮箱", example = "zhangsan@itcast.cn")
    @NotBlank(message = "邮箱不能为空", groups = {ValidEmail.class})
    @Email(groups = {ValidEmail.class})
    private String email;

    @ApiModelProperty(value = "QQ唯一标识")
    private String qq;

    @ApiModelProperty(value = "微信唯一标识")
    private String weixin;

    @ApiModelProperty(value = "新浪微博唯一标识")
    private String weibo;

    @ApiModelProperty(value = "认证信息ID", example = "10000")
    private String authId;

    @ApiModelProperty(value = "操作所在的ip", example = "192.168.1.1")
    private String ip;

    @ApiModelProperty(value = "所在经度", example = "116.350496")
    private String longitude;

    @ApiModelProperty(value = "所在纬度", example = "40.066241")
    private String latitude;
}
