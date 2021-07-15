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
