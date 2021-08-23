package com.mo.controller;

import com.mo.dto.AuthDTO;
import com.mo.entity.Auth;
import com.mo.model.Result;
import com.mo.model.ResultCode;
import com.mo.model.ResultPage;
import com.mo.request.AuthRequest;
import com.mo.request.UserLoginRequest;
import com.mo.request.UserRegisterRequest;
import com.mo.service.AuthService;
import com.mo.validate.ValidEmail;
import com.mo.validate.ValidMobile;
import com.mo.validate.ValidUserName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.guieffect.qual.PolyUIEffect;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Created by mo on 2021/7/15
 */
@Api(tags = "认证信息模块")
@RequestMapping("/api/auth/v1")
@Slf4j
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;


    @ApiOperation("QQ登录/注册接口")
    @PostMapping("/loginQQ")
    public Result loginQQ(@RequestBody UserLoginRequest request) {
        return authService.loginQQ(request);
    }

    @ApiOperation("解绑新浪微博")
    @PostMapping("/unbindWb")
    public Result unbindWb(@RequestBody UserLoginRequest request) {
        return authService.unbindWb(request);
    }

    @ApiOperation("刷新用户微博个人信息(调用接口刷新)")
    @GetMapping("/refreshWbInfo/{authId}")
    public Result refreshWbInfo(@PathVariable String authId) {
        return authService.refreshWbInfo(authId);
    }

    @ApiOperation("获取用户微博个人信息(直接查询数据库)")
    @PostMapping("/queryWbInfo")
    public Result queryWbInfo(@RequestBody UserLoginRequest request) {
        return authService.queryWbInfo(request);
    }

    @ApiOperation("微博登陆/注册接口")
    @PostMapping("/loginWB")
    public Result loginWB(@RequestBody UserLoginRequest request) {
        return authService.loginWB(request);
    }

    @ApiOperation("根据id修改用户认证信息")
    @PostMapping("/updateAuth")
    public Result updateAuth(@RequestBody UserRegisterRequest request) {
        return authService.updateAuth(request);
    }

    @ApiOperation("根据Email邮箱验证码修改密码")
    @PostMapping("/pwdByEmail")
    public Result pwdByEmail(@RequestBody UserLoginRequest request) {
        return authService.pwdByEmail(request);
    }

    @ApiOperation("根据手机号验证码修改密码")
    @PostMapping("/pwdByMobile")
    public Result pwdByMobile(@RequestBody UserLoginRequest request) {
        return authService.pwdByMobile(request);
    }

    @ApiOperation("根据旧密码修改密码")
    @PostMapping("/pwdByOld")
    public Result pwdByOld(@RequestBody UserLoginRequest request) {
        return authService.pwdByOld(request);
    }

    @ApiOperation("用户注销")
    @PostMapping("/logout")
    public Result logout(@RequestBody UserLoginRequest request) {
        return authService.logout(request);
    }


    @ApiOperation("根据认证信息主键ID解绑微信")
    @PostMapping("/unbindWx")
    public Result unbindWx(@RequestBody UserLoginRequest request) {
        return authService.unbindWx(request);
    }

    @ApiOperation("刷新用户个人信息(调用微信接口查询)")
    @PostMapping("/refreshWxInfo")
    public Result refreshWxInfo(@RequestBody UserLoginRequest request) {
        return authService.refreshWxInfo(request);
    }

    @ApiOperation("查询用户微信个人信息(从数据库查询)")
    @PostMapping("/queryWxInfo")
    public Result queryWxInfo(@RequestBody UserLoginRequest request) {
        return authService.queryWxInfo(request);
    }


    @ApiOperation("根据认证信息主键刷新接口调用凭证access_token")
    @GetMapping("/wxToken/{authId}")
    public Result refreshWxToken(@PathVariable String authId) {

        return authService.refreshWxToken(authId);
    }

    @ApiOperation("微信扫码登录/注册接口")
    @PostMapping("/loginWX")
    public Result<AuthDTO> loginWX(@ApiParam(value = "用户登录请求对象", required = true)
                                   @Validated
                                   @RequestBody UserLoginRequest request) {

        Result<AuthDTO> authDTO = authService.loginWX(request);
        return authDTO;

    }


    @ApiOperation("用户登录-手机验证码登录")
    @PostMapping("/loginByMobile")
    public Result<AuthDTO> loginByMobile(@ApiParam(value = "用户登录请求对象", required = true)
                                         @Validated
                                         @RequestBody UserLoginRequest request) {
        Result<AuthDTO> authDTO = authService.loginByMobile(request);
        return authDTO;

    }

    @ApiOperation("用户登录-用户账户密码登录")
    @PostMapping("/login")
    public Result<AuthDTO> login(
            @ApiParam(value = "用户登录请求对象", required = true)
            @Validated
            @RequestBody UserLoginRequest request) {

        Result<AuthDTO> authDTO = authService.login(request);
        return authDTO;

    }


    @ApiOperation("用户注册-手机号注册")
    @PostMapping("/registerByMobile")
    public Result<AuthDTO> registerByMobile(@ApiParam(value = "用户注册请求对象", required = true)
                                            @Validated(ValidMobile.class)
                                            @RequestBody UserRegisterRequest request) {

        Result<AuthDTO> authDTO = authService.registerByMobile(request);
        return authDTO;
    }

    @ApiOperation("用户注册-邮箱注册")
    @PostMapping("/registerByEmail")
    public Result<AuthDTO> registerByEmail(@ApiParam(value = "用户注册请求对象", required = true)
                                           @Validated(ValidEmail.class)
                                           @RequestBody UserRegisterRequest request) {
        Result<AuthDTO> authDTO = authService.registerByEmail(request);
        return authDTO;
    }


    @ApiOperation("用户注册-用户名注册")
    @PostMapping("/registerByUserName")
    public Result<AuthDTO> registerByUserName(@ApiParam(value = "用户注册请求对象", required = true)
                                              @Validated(ValidUserName.class)
                                              @RequestBody UserRegisterRequest request) {

        Result<AuthDTO> authDTO = authService.registerByUserName(request);
        return authDTO;
    }

    @ApiOperation("根据条件查询认证信息")
    @PostMapping("/query")
    public Result<AuthDTO> query(@ApiParam("用户认证信息请求对象") @Validated @RequestBody AuthRequest request) {

        Result<AuthDTO> authDTO = authService.query(request);
        return authDTO;

    }

    @ApiOperation("根据条件分页查询认证信息")
    @PostMapping("/pageAuthList")
    public ResultPage<AuthDTO> pageAuthList(@ApiParam("用户认证信息请求对象") @Validated @RequestBody AuthRequest request) {

        ResultPage<AuthDTO> pageResult = authService.pageAuthList(request);
        return pageResult;

    }

}
