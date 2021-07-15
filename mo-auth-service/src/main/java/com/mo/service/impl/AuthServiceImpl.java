package com.mo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.entity.Auth;
import com.mo.mapper.AuthMapper;
import com.mo.service.AuthService;
import org.springframework.stereotype.Service;

/**
 * Created by mo on 2021/7/15
 */
@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, Auth> implements AuthService {
}
