package com.mo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.dto.AuthDTO;
import com.mo.entity.Auth;
import com.mo.model.ResultPage;
import com.mo.request.AuthListRequest;

/**
 * Created by mo on 2021/7/15
 * 用户认证信息操作 Service接口
 */
public interface AuthService extends IService<Auth> {
    ResultPage<AuthDTO> pageAuthList(AuthListRequest request);
}
