package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.constant.CacheKey;
import com.mo.dto.AuthDTO;
import com.mo.entity.Auth;
import com.mo.enums.BizCodeEnum;
import com.mo.exception.BizException;
import com.mo.mapper.AuthMapper;
import com.mo.model.Result;
import com.mo.model.ResultCode;
import com.mo.model.ResultPage;
import com.mo.request.AuthRequest;
import com.mo.request.UserRegisterRequest;
import com.mo.service.AuthService;
import com.mo.utils.IdWorker;
import com.mo.utils.MacUtil;
import com.mo.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mo on 2021/7/15
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthMapper authMapper;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 用户注册
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result<AuthDTO> register(UserRegisterRequest request) {

        //获取图片验证码的key
        String key = request.getKey();
        log.debug("用户名注册：key={},code={}", key, request.getCode());

        //从redis中获取code
        String cacheCode = redisUtil.get(key);
        String code = request.getCode();
        //判断用户传递的code验证码是否正确
        if (!code.equals(cacheCode)) {
            throw new BizException(BizCodeEnum.CODE_ERROR);
        }

        //判断用户是否重复，用户名应该是唯一的
        QueryWrapper<Auth> wrapper = new QueryWrapper<>();
        wrapper.eq(request.getUserName() != null, "user_name", request.getUserName());
        Integer result = authMapper.selectCount(wrapper);
        if (result > 0) {
            throw new BizException(BizCodeEnum.USER_EXISTS);
        }

//        //获取主键id
//        String id = idWorker.nextId() + "";
        //用户密码进行加密
        String password = MacUtil.makeHashPassword(request.getPassword());

        //保存注册信息
        Auth auth = Auth.builder()
                .userName(request.getUserName())
                .password(password)
                .status(1)
                .createDate(new Date())
                .lastDate(new Date())
                .build();
        authMapper.insert(auth);

        AuthDTO authDTO = new AuthDTO();
        BeanUtils.copyProperties(auth, authDTO);

        return Result.success("账户注册成功", authDTO);
    }

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
        return Result.buildCodeAndMsg(ResultCode.NOT_FOUND, "数据查询失败");
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
