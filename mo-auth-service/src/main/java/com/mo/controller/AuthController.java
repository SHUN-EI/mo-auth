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
