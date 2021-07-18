package com.mo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.dto.AuthDTO;
import com.mo.entity.Auth;
import com.mo.model.Result;
import com.mo.model.ResultPage;
import com.mo.request.AuthRequest;

/**
 * Created by mo on 2021/7/15
 * 用户认证信息操作 Service接口
 */
public interface AuthService {
    ResultPage<AuthDTO> pageAuthList(AuthRequest request);

    Result<AuthDTO> query(AuthRequest request);
}
