package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.dto.AuthDTO;
import com.mo.entity.Auth;
import com.mo.mapper.AuthMapper;
import com.mo.model.ResultPage;
import com.mo.request.AuthListRequest;
import com.mo.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mo on 2021/7/15
 */
@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, Auth> implements AuthService {

    @Override
    public ResultPage<AuthDTO> pageAuthList(AuthListRequest request) {

        Page<Auth> page = new Page<>(request.getPageNum(), request.getPageSize());

        //封装查询条件
        QueryWrapper<Auth> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("last_date");


        //封装查询条件，不为null作为查询条件
        wrapper.eq(request.getId() != null, "id", request.getId());
        wrapper.eq(request.getUserName() != null, "user_name", request.getUserName());
        wrapper.eq(request.getMobile() != null, "mobile", request.getMobile());
        wrapper.eq(request.getEmail() != null, "email", request.getEmail());
        wrapper.eq(request.getWeixin() != null, "weixin", request.getWeixin());
        wrapper.eq(request.getWeibo() != null, "weibo", request.getWeibo());
        wrapper.eq(request.getQq() != null, "qq", request.getQq());
        wrapper.eq(request.getStatus() != null, "status", request.getStatus());

        wrapper.ge(request.getMobileBindDate() != null, "mobile_bind_date", request.getMobileBindDate());
        wrapper.ge(request.getEmailBindDate() != null, "email_bind_date", request.getEmailBindDate());
        wrapper.ge(request.getWeiboBindDate() != null, "weixin_bind_date", request.getWeiboBindDate());
        wrapper.ge(request.getWeiboBindDate() != null, "weibo_bind_date", request.getWeiboBindDate());
        wrapper.ge(request.getQqBindDate() != null, "qq_bind_date", request.getQqBindDate());
        wrapper.ge(request.getCreateDate() != null, "create_date", request.getCreateDate());
        wrapper.ge(request.getLastDate() != null, "last_date", request.getLastDate());

        page = page(page, wrapper);
        List<Auth> authList = page.getRecords();
        List<AuthDTO> authDTOList = authList.stream().map(obj -> {

            AuthDTO authDTO = new AuthDTO();
            BeanUtils.copyProperties(obj, authDTO);
            return authDTO;
        }).collect(Collectors.toList());

        return ResultPage.success(page.getTotal(), page.getPages(),
                request.getPageSize(), request.getPageNum(), authDTOList);
    }
}
