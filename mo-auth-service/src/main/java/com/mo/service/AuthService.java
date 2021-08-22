package com.mo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.dto.AuthDTO;
import com.mo.entity.Auth;
import com.mo.model.Result;
import com.mo.model.ResultPage;
import com.mo.request.AuthRequest;
import com.mo.request.UserLoginRequest;
import com.mo.request.UserRegisterRequest;

/**
 * Created by mo on 2021/7/15
 * 用户认证信息操作 Service接口
 */
public interface AuthService {
    ResultPage<AuthDTO> pageAuthList(AuthRequest request);

    Result<AuthDTO> query(AuthRequest request);

    Result<AuthDTO> registerByUserName(UserRegisterRequest request);

    Result<AuthDTO> registerByEmail(UserRegisterRequest request);

    Result<AuthDTO> registerByMobile(UserRegisterRequest request);

    Result<AuthDTO> login(UserLoginRequest request);

    Result<AuthDTO> loginByMobile(UserLoginRequest request);

    Result<AuthDTO> loginWX(UserLoginRequest request);

    Result refreshWxToken(String authId);

    Result queryWxInfo(UserLoginRequest request);

    Result refreshWxInfo(UserLoginRequest request);

    Result unbindWx(UserLoginRequest request);

    Result logout(UserLoginRequest request);

    Result pwdByOld(UserLoginRequest request);

    Result pwdByMobile(UserLoginRequest request);

    Result pwdByEmail(UserLoginRequest request);

    Result updateAuth(UserRegisterRequest request);

    Result loginWB(UserLoginRequest request);

    Result queryWbInfo(UserLoginRequest request);

    Result refreshWbInfo(String authId);
}
