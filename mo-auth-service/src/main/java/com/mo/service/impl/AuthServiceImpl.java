package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.dto.AuthDTO;
import com.mo.entity.Auth;
import com.mo.mapper.AuthMapper;
import com.mo.model.Result;
import com.mo.model.ResultCode;
import com.mo.model.ResultPage;
import com.mo.request.AuthRequest;
import com.mo.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mo on 2021/7/15
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthMapper authMapper;

    /**
     * 根据条件查询认证信息
     *
     * @param request
     * @return
     */
    @Override
    public Result<AuthDTO> query(AuthRequest request) {

        //封装查询条件
        QueryWrapper<Auth> wrapper = new QueryWrapper<>();

        //封装查询条件，不为null作为查询条件
        wrapper.eq(request.getId() != null, "id", request.getId());
        wrapper.eq(request.getMobile() != null, "mobile", request.getMobile());
        wrapper.eq(request.getEmail() != null, "email", request.getEmail());
        wrapper.eq(request.getUserName() != null, "user_name", request.getUserName());

        Auth auth = authMapper.selectOne(wrapper);
        if (auth != null) {

            AuthDTO authDTO = new AuthDTO();
            BeanUtils.copyProperties(auth, authDTO);
            return Result.success("查询成功", authDTO);

        }
        return Result.error(ResultCode.NOT_FOUND, "数据查询失败");
    }

    /**
     * 根据条件分页查询认证信息
     *
     * @param request
     * @return
     */
    @Override
    public ResultPage<AuthDTO> pageAuthList(AuthRequest request) {

        Page<Auth> pageInfo = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<Auth> resultPage = null;

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

        resultPage = authMapper.selectPage(pageInfo, wrapper);
        List<Auth> authList = resultPage.getRecords();
        List<AuthDTO> authDTOList = authList.stream().map(obj -> {

            AuthDTO authDTO = new AuthDTO();
            BeanUtils.copyProperties(obj, authDTO);
            return authDTO;
        }).collect(Collectors.toList());

        return ResultPage.success(resultPage.getTotal(), resultPage.getPages(),
                request.getPageSize(), request.getPageNum(), authDTOList);
    }
}
