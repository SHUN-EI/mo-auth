package com.mo.controller;

import com.mo.dto.AuthDTO;
import com.mo.entity.Auth;
import com.mo.mapper.AuthMapper;
import com.mo.model.Result;
import com.mo.model.ResultCode;
import com.mo.model.ResultPage;
import com.mo.request.AuthListRequest;
import com.mo.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @ApiOperation("根据条件分页查询认证信息")
    @PostMapping("/pageAuthList")
    public ResultPage<AuthDTO> pageAuthList(@RequestBody AuthListRequest request) {

        ResultPage<AuthDTO> pageResult = authService.pageAuthList(request);
        return pageResult;

    }

    @ApiOperation("根据用户ID查询认证信息")
    @PostMapping("/query/id")
    public Result<AuthDTO> findByid(@RequestParam String id) {

        Auth auth = authService.getById(id);

        if (auth != null) {
            AuthDTO authDTO = new AuthDTO();
            BeanUtils.copyProperties(auth, authDTO);
            return Result.success("查询成功", authDTO);
        }

        return Result.error(ResultCode.NOT_FOUND, "没有查询到数据");

    }
}
