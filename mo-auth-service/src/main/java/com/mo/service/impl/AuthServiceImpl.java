package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.constant.CacheKey;
import com.mo.dto.AuthDTO;
import com.mo.entity.Auth;
import com.mo.enums.AccountStatus;
import com.mo.enums.AuthType;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.SendCodeEnum;
import com.mo.exception.BizException;
import com.mo.mapper.AuthMapper;
import com.mo.model.Result;
import com.mo.model.ResultCode;
import com.mo.model.ResultPage;
import com.mo.request.AuthRequest;
import com.mo.request.UserLoginRequest;
import com.mo.request.UserRegisterRequest;
import com.mo.service.AuthService;
import com.mo.utils.IdWorker;
import com.mo.utils.JWTUtil;
import com.mo.utils.MacUtil;
import com.mo.utils.RedisUtil;
import com.mo.validate.Mobile;
import com.mo.validate.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
     * 用户登录-手机验证码登录
     *
     * @param request
     * @return
     */
    @Override
    public Result<AuthDTO> loginByMobile(UserLoginRequest request) {

        //手机号获取
        String mobile = request.getMobile();

        //手机号验证码缓存key
        String cachekey = String.format(CacheKey.CHECK_CODE_KEY, SendCodeEnum.USER_REGISTER, mobile);

        //校验验证码
        String cacheCode = redisUtil.get(cachekey);
        if (!request.getCode().equals(cacheCode)) {
            throw new BizException(BizCodeEnum.CODE_ERROR);
        }

        //根据手机号查询用户
        QueryWrapper<Auth> wrapper = new QueryWrapper<>();
        wrapper.eq(request.getMobile() != null, "mobile", request.getMobile());
        Auth auth = authMapper.selectOne(wrapper);

        //如果用户查询不到结果，说明手机号未注册
        if (null == auth) {
            //执行注册操作
            UserRegisterRequest registerRequest = new UserRegisterRequest();
            BeanUtils.copyProperties(request, registerRequest);
            return registerByMobile(registerRequest);
        }

        //更新最后一次登录时间
        authMapper.updateLastDate(auth.getId());

        AuthDTO authDTO = new AuthDTO();
        BeanUtils.copyProperties(auth, authDTO);

        String token = JWTUtil.generateJsonWebToken(auth.getId());
        authDTO.setToken(token);

        return Result.success("账户登录成功", authDTO);
    }

    /**
     * 用户登录-用户账户密码登录
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result<AuthDTO> login(UserLoginRequest request) {

        //获取图片验证码
        String key = request.getKey();
        String code = request.getCode();

        //判断图片验证码是否正确
        String cacheCode = redisUtil.get(key);
        if (!code.equals(cacheCode)) {
            throw new BizException(BizCodeEnum.CODE_ERROR);
        }

        //判断登录类型
        String input = request.getInput();
        AuthType authType = ValidateUtil.chechLogin(input);

        QueryWrapper<Auth> wrapper = new QueryWrapper<>();

        //根据登录类型，分别进行用户认证信息的查询
        Auth auth;
        switch (authType) {
            case MOBILE:
                wrapper.eq(input != null, "mobile", input);
                auth = authMapper.selectOne(wrapper);
                break;
            case USERNAME:
                wrapper.eq(input != null, "user_name", input);
                auth = authMapper.selectOne(wrapper);
                break;
            default:
                wrapper.eq(input != null, "email", input);
                auth = authMapper.selectOne(wrapper);
                break;
        }

        //账户不存在
        if (null == auth) {
            throw new BizException(BizCodeEnum.ACCOUNT_UNREGISTER);
        }

        //如果账户不为空，进行密码校验
        String password = MacUtil.makeHashPassword(request.getPassword());
        if (!password.equals(auth.getPassword())) {
            throw new BizException(BizCodeEnum.ACCOUNT_PWD_ERROR);
        }

        //校验账户的状态
        if (AccountStatus.FORBIDDEN.getStatus() == auth.getStatus()) {
            throw new BizException(BizCodeEnum.ACCOUNT_FORBIDDEN);
        }

        //更新最后一次登录时间
        authMapper.updateLastDate(auth.getId());

        AuthDTO authDTO = new AuthDTO();
        BeanUtils.copyProperties(auth, authDTO);

        String token = JWTUtil.generateJsonWebToken(auth.getId());
        authDTO.setToken(token);

        return Result.success("账户登录成功", authDTO);
    }

    /**
     * 用户注册-手机号注册
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result<AuthDTO> registerByMobile(UserRegisterRequest request) {

        //获取手机号
        String mobile = request.getMobile();
        log.info("手机号注册,mobile={},code={}", mobile, request.getCode());

        //手机号验证码缓存key
        String cachekey = String.format(CacheKey.CHECK_CODE_KEY, SendCodeEnum.USER_REGISTER, mobile);

        //获取redis中的验证码
        String cacheCode = redisUtil.get(cachekey);
        //判断用户传递的code验证码是否正确
        if (!request.getCode().equals(cacheCode)) {
            throw new BizException(BizCodeEnum.CODE_ERROR);
        }

        //判断手机号是否重复,手机号应该是唯一的
        QueryWrapper<Auth> wrapper = new QueryWrapper<>();
        wrapper.eq(request.getMobile() != null, "mobile", request.getMobile());

        Integer result = authMapper.selectCount(wrapper);
        if (result > 0) {
            throw new BizException(BizCodeEnum.USER_MOBILE_EXISTS);
        }

        //保存注册信息
        Auth auth = Auth.builder()
                .mobile(request.getMobile())
                .mobileBindDate(new Date())
                .status(1)
                .createDate(new Date())
                .lastDate(new Date())
                .build();

        //手机号注册，可以使用密码，也可以不使用密码
        //如果有密码，则进行密码加密
        if (StringUtils.isNotBlank(request.getPassword())) {
            String password = MacUtil.makeHashPassword(request.getPassword());
            auth.setPassword(password);
        }

        authMapper.insert(auth);

        AuthDTO authDTO = new AuthDTO();
        BeanUtils.copyProperties(auth, authDTO);

        String token = JWTUtil.generateJsonWebToken(auth.getId());
        authDTO.setToken(token);

        return Result.success("账户注册成功", authDTO);
    }

    /**
     * 用户注册-邮箱注册
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result<AuthDTO> registerByEmail(UserRegisterRequest request) {

        //获取电子邮箱
        String email = request.getEmail();
        log.info("邮箱注册:email={},code={}", email, request.getCode());

        //邮箱验证码缓存key
        String cachekey = String.format(CacheKey.CHECK_CODE_KEY, SendCodeEnum.USER_REGISTER, email);

        //获取redis中的验证码
        String cacheCode = redisUtil.get(cachekey);
        //判断用户传递的code验证码是否正确
        if (!request.getCode().equals(cacheCode)) {
            throw new BizException(BizCodeEnum.CODE_ERROR);
        }

        //判断Email是否重复,邮箱应该是唯一的
        QueryWrapper<Auth> wrapper = new QueryWrapper<>();
        wrapper.eq(request.getEmail() != null, "email", request.getEmail());

        Integer result = authMapper.selectCount(wrapper);
        if (result > 0) {
            throw new BizException(BizCodeEnum.USER_EMAIL_EXISTS);
        }

        //用户密码进行加密
        String password = MacUtil.makeHashPassword(request.getPassword());

        //保存注册信息
        Auth auth = Auth.builder()
                .email(email)
                .emailBindDate(new Date())
                .password(password)
                .status(1)
                .createDate(new Date())
                .lastDate(new Date())
                .build();
        authMapper.insert(auth);

        AuthDTO authDTO = new AuthDTO();
        BeanUtils.copyProperties(auth, authDTO);

        String token = JWTUtil.generateJsonWebToken(auth.getId());
        authDTO.setToken(token);

        return Result.success("账户注册成功", authDTO);

    }

    /**
     * 用户注册-用户名注册
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result<AuthDTO> registerByUserName(UserRegisterRequest request) {

        //获取图片验证码的key
        String key = request.getKey();
        log.info("用户名注册：key={},code={}", key, request.getCode());

        //从redis中获取code
        String cacheCode = redisUtil.get(key);
        //判断用户传递的code验证码是否正确
        if (!request.getCode().equals(cacheCode)) {
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

        String token = JWTUtil.generateJsonWebToken(auth.getId());
        authDTO.setToken(token);

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
