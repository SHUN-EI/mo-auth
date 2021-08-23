package com.mo.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.aop.LoginRec;
import com.mo.config.QQConfig;
import com.mo.config.WBConfig;
import com.mo.config.WXConfig;
import com.mo.constant.CacheKey;
import com.mo.dto.AuthDTO;
import com.mo.entity.Auth;
import com.mo.entity.QQInfo;
import com.mo.entity.WbInfo;
import com.mo.entity.WxInfo;
import com.mo.enums.*;
import com.mo.exception.BizException;
import com.mo.mapper.AuthMapper;
import com.mo.mapper.QQInfoMapper;
import com.mo.mapper.WbInfoMapper;
import com.mo.mapper.WxInfoMapper;
import com.mo.model.Result;
import com.mo.model.ResultCode;
import com.mo.model.ResultPage;
import com.mo.request.AuthRequest;
import com.mo.request.UserLoginRequest;
import com.mo.request.UserRegisterRequest;
import com.mo.service.AuthService;
import com.mo.utils.*;
import com.mo.validate.Mobile;
import com.mo.validate.UserName;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private WXConfig wxConfig;
    @Autowired
    private WxInfoMapper wxInfoMapper;
    @Autowired
    private WBConfig wbConfig;
    @Autowired
    private WbInfoMapper wbInfoMapper;
    @Autowired
    private QQConfig qqConfig;
    @Autowired
    private QQInfoMapper qqInfoMapper;

    /**
     * 刷新用户的QQ个人信息(调用QQ接口)
     *
     * @param authId
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result refreshQQInfo(String authId) {
        //根据authId查询QQ个人信息
        QQInfo qqInfo = qqInfoMapper.selectById(authId);

        //判断QQ个人信息是否为空
        if (qqInfo == null) {
            return Result.error("用户未绑定QQ");
        }

        //判断接口调用凭证access_token是否过期,减100秒，预留极限时间
        Instant expire = qqInfo.getAccessTokenDate().toInstant().plusSeconds(qqInfo.getExpiresIn() - 100);

        if (new Date().toInstant().compareTo(expire) > 0) {
            //如果过期，重新刷新
            String url = qqConfig.getAccessTokenUrl() +
                    "?grant_type=refresh_token" +
                    "&client_id=" + qqConfig.getAppid() +
                    "&client_secret=" + qqConfig.getAppKey() +
                    "&refresh_token=" + qqInfo.getRefreshToken();

            Map<String, String> accessTokenMap = getQQAccessToken(url);

            //判断获取到的access_token是否存在
            String accessToken = accessTokenMap.get("access_token");
            String refreshToken = accessTokenMap.get("refresh_token");
            String expiresIn = accessTokenMap.get("expires_in");
            if (StringUtils.isBlank(accessToken)) {
                return Result.error("QQ登录，获取access_token失败");
            }

            //设置接口调用凭证access_token
            qqInfo.setAccessToken(accessToken);
            qqInfo.setAccessTokenDate(new Date());
            qqInfo.setRefreshToken(refreshToken);
            qqInfo.setExpiresIn(Integer.parseInt(expiresIn));
        }

        //查询QQ个人信息
        String infoUrl = qqConfig.getQqInfoUrl() +
                "?access_token=" + qqInfo.getAccessToken() +
                "&oauth_consumer_key=" + qqConfig.getAppid() +
                "&openid=" + qqInfo.getOpenid();

        Map<String, Object> resultMap = HttpUtil.sendGet(infoUrl);
        Object nickname = resultMap.get("nickname");

        //判断是否查询成功
        if (nickname == null) {
            return Result.error("查询QQ个人信息错误");
        }

        //保存查询到的QQ个人信息
        qqInfo.setNickname(nickname.toString());
        qqInfo.setGender(resultMap.get("gender").toString());
        qqInfo.setProvince(resultMap.get("province").toString());
        qqInfo.setCity(resultMap.get("city").toString());
        qqInfo.setUpdateDate(new Date());

        //返回结果
        return Result.success("QQ个人信息查询成功", qqInfo);
    }

    /**
     * 查询用户的QQ个人信息(查询数据库)
     *
     * @param authId
     * @return
     */
    @Override
    public Result queryQQInfo(String authId) {

        QQInfo qqInfo = qqInfoMapper.selectById(authId);
        if (qqInfo == null) {
            return Result.error("用户未绑定QQ");
        }

        return Result.success("查询成功", qqInfo);
    }


    /**
     * QQ登录/注册接口
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result loginQQ(UserLoginRequest request) {
        //根据code  获取 access_token
        String url = qqConfig.getAccessTokenUrl() +
                "?grant_type=authorization_code" +
                "&client_id=" + qqConfig.getAppid() +
                "&client_secret=" + qqConfig.getAppKey() +
                "&code=" + request.getCode() +
                "&redirect_uri=" + qqConfig.getRedirectUri();

        Map<String, String> accessTokenMap = getQQAccessToken(url);
        String accessToken = accessTokenMap.get("access_token");
        String expiresIn = accessTokenMap.get("expires_in");
        String refreshToken = accessTokenMap.get("refresh_token");

        //判断结果是否为空
        if (StringUtils.isBlank(accessToken)) {
            return Result.error("QQ登录，获取access_token失败");
        }

        //根据access_token获取openid
        String openid = null;
        try {
            String openidUrl = qqConfig.getOpenIdUrl() + "?access_token=" + accessToken;
            String result = HttpUtil.sendGetStr(openidUrl);

            //返回callback( {"client_id":"101859954","openid":"DEB0177D02FD329483BFC17FD66A52DA"} );
            String openidJson = result.substring(result.indexOf("{"), result.indexOf("}") + 1);
            openid = JSON.parseObject(openidJson).get("openid").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //判断结果是否为空
        if (StringUtils.isBlank(openid)) {
            return Result.error("QQ登录，获取openid失败");
        }

        //根据openid查询用户信息是否存在
        QueryWrapper<Auth> wrapper = new QueryWrapper<>();
        wrapper.eq(openid != null, "qq", openid);
        Auth auth = authMapper.selectOne(wrapper);

        //判断查询结果是否不为空,用户已经绑定QQ
        if (auth != null) {
            //更新登录时间
            authMapper.updateLastDate(request.getAuthId());

            //更新接口调用凭证access_token
            QueryWrapper<QQInfo> qqWrapper = new QueryWrapper<>();
            qqWrapper.eq(openid != null, "openid", openid);
            QQInfo qqInfo = qqInfoMapper.selectOne(qqWrapper);

            if (qqInfo != null) {
                qqInfo.setAccessToken(accessToken);
                qqInfo.setAccessTokenDate(new Date());
                qqInfo.setExpiresIn(Integer.parseInt(expiresIn));
                qqInfo.setRefreshToken(refreshToken);
                qqInfo.setUpdateDate(new Date());
                qqInfoMapper.updateById(qqInfo);
            }
        } else {
            //如果查询结果为空，表示用户第一次QQ登录
            //判断authId是否不为空
            if (StringUtils.isNotBlank(request.getAuthId())) {
                //如果authId不为空，表示用户已经登录，绑定qq
                auth = authMapper.selectById(request.getAuthId());
                auth.setQq(openid);
                auth.setQqBindDate(new Date());
                auth.setLastDate(new Date());

                authMapper.updateById(auth);

            } else {
                //如果authId为空，表示用户未登录，进行注册
                auth = Auth.builder()
                        .qq(openid)
                        .qqBindDate(new Date())
                        .status(1)
                        .createDate(new Date())
                        .lastDate(new Date())
                        .build();

                authMapper.insert(auth);
            }

            //保存用户的QQ个人信息
            QQInfo qqInfo = QQInfo.builder()
                    .authId(auth.getId())
                    .openid(openid)
                    .accessToken(accessToken)
                    .accessTokenDate(new Date())
                    .expiresIn(Integer.parseInt(expiresIn))
                    .refreshToken(refreshToken)
                    .createDate(new Date())
                    .updateDate(new Date())
                    .build();

            qqInfoMapper.insert(qqInfo);
        }

        AuthDTO authDTO = new AuthDTO();
        BeanUtils.copyProperties(auth, authDTO);

        String token = JWTUtil.generateJsonWebToken(auth.getId());
        authDTO.setToken(token);

        //把token保存到redis中，用于注销功能
        redisUtil.set(CacheKey.getJwtToken(auth.getId()), token, CacheKey.TOKENEXPIRETIME);

        return Result.success("QQ登录成功", authDTO);
    }

    /**
     * 解析QQAccessToken 的返回结果
     *
     * @param url
     * @return
     */
    private Map<String, String> getQQAccessToken(String url) {
        Map<String, String> map = new HashMap<>();
        String result = HttpUtil.sendGetStr(url);

        //access_token=A54A1A2F26D2CCA2849442D2274B3240&expires_in=7776000&refresh_token=E46A43A4FB5547BB1FB6C63C4656D331
        String[] splitArr = result.split("&");
        for (int i = 0; i < splitArr.length; i++) {
            //access_token=A54A1A2F26D2CCA2849442D2274B324
            String[] s = splitArr[i].split("=");
            map.put(s[0], s[1]);
        }
        return map;

    }

    /**
     * 解绑新浪微博
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result unbindWb(UserLoginRequest request) {

        Auth auth = authMapper.selectById(request.getAuthId());
        if (auth == null) {
            return Result.error("用户不存在");
        }

        //删除认证信息的微博唯一标识
        auth.setWeibo(null);
        auth.setWeiboBindDate(null);
        authMapper.updateById(auth);

        //删除微博个人信息数据
        wbInfoMapper.deleteById(request.getAuthId());

        return Result.success("解绑微博成功");
    }

    /**
     * 刷新用户微博个人信息(调用接口刷新)
     *
     * @param authId
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result refreshWbInfo(String authId) {
        //获取微博个人信息
        WbInfo wbInfo = wbInfoMapper.selectById(authId);

        //判断是否查询到结果
        if (wbInfo == null) {
            return Result.error("用户未绑定新浪微博");
        }

        //判断接口调用凭证access_token是否过期,减100秒，预留极限时间
        Instant expire = wbInfo.getAccessTokenDate().toInstant().plusSeconds(wbInfo.getExpiresIn() - 100);

        if (new Date().toInstant().compareTo(expire) > 0) {
            //access_token过期时间达到后，access_token就失效了
            return Result.error("新浪微博access_token接口调用凭证失效");
        }

        //调用接口查询微博个人信息
        String url = wbConfig.getWbInfoUrl() +
                "?access_token=" + wbInfo.getAccessToken() +
                "&uid=" + wbInfo.getUid();

        Map<String, Object> map = HttpUtil.sendGet(url);
        Object idstr = map.get("idstr");

        //判断是否查询到数据，接口调用是否成功
        if (idstr == null || !wbInfo.getUid().equals(idstr.toString())) {
            return Result.error("查询微博个人信息错误");
        }

        //保存获取到的微博个人信息
        String screenName = map.get("screen_name").toString();
        String location = map.get("location").toString();
        String profileImageUrl = map.get("profile_image_url").toString();
        String avatarHd = map.get("avatar_hd").toString();
        String gender = map.get("gender").toString();
        String lang = map.get("lang").toString();

        wbInfo.setScreenName(screenName);
        wbInfo.setLocation(location);
        wbInfo.setProfileImageUrl(profileImageUrl);
        wbInfo.setAvatarHd(avatarHd);
        wbInfo.setGender(gender);
        wbInfo.setLang(lang);
        wbInfo.setUpdateDate(new Date());
        wbInfoMapper.updateById(wbInfo);

        return Result.success("获取最新微博个人信息成功", wbInfo);
    }

    /**
     * 获取用户微博个人信息(直接查询数据库)
     *
     * @param request
     * @return
     */
    @Override
    public Result queryWbInfo(UserLoginRequest request) {
        WbInfo wbInfo = wbInfoMapper.selectById(request.getAuthId());

        if (wbInfo == null) {
            return Result.error("用户未绑定新浪微博");
        }

        return Result.success("查询成功", wbInfo);

    }

    /**
     * 微博登陆/注册接口
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result loginWB(UserLoginRequest request) {
        //调用微博接口，使用code获取access_token和uid
        Map params = new HashMap();
        params.put("client_id", wbConfig.getAppid());
        params.put("client_secret", wbConfig.getSecret());
        params.put("grant_type", "authorization_code");
        params.put("code", request.getCode());
        params.put("redirect_uri", wbConfig.getRedirectUri());

        //发送请求
        Map map = HttpUtil.sendPost(wbConfig.getAccessTokenUrl(), params);
        Object accessToken = map.get("access_token");
        Object uid = map.get("uid");
        int expiresIn = Integer.parseInt(map.get("expires_in").toString());

        //根据access_token 和 uid判断接口调用是否成功
        if (accessToken == null || uid == null) {
            return Result.error("微博登陆获取access_token失败");
        }

        //根据uid查询用户是否存在
        QueryWrapper<Auth> wrapper = new QueryWrapper<>();
        wrapper.eq(uid != null, "weibo", uid.toString());
        Auth auth = authMapper.selectOne(wrapper);

        //如果存在，则表示用户登录成功
        if (auth != null) {
            //更新登录时间
            authMapper.updateLastDate(auth.getId());

            //更新接口调用凭证access_token
            QueryWrapper<WbInfo> wbWrapper = new QueryWrapper<>();
            wbWrapper.eq(uid != null, "uid", uid.toString());
            WbInfo wbInfo = wbInfoMapper.selectOne(wbWrapper);

            if (wbInfo != null) {
                wbInfo.setAccessToken(accessToken.toString());
                wbInfo.setAccessTokenDate(new Date());
                wbInfo.setExpiresIn(expiresIn);
                wbInfo.setUpdateDate(new Date());
                wbInfoMapper.updateById(wbInfo);
            }
        } else {
            //如果不存在,判断authId是否为空
            if (StringUtils.isNotBlank(request.getAuthId())) {

                //如果authId不为空，表示用户已经登录，绑定微博
                auth = authMapper.selectById(request.getAuthId());
                auth.setWeibo(uid.toString());
                auth.setWeiboBindDate(new Date());
                auth.setLastDate(new Date());

                authMapper.updateById(auth);
            } else {
                //如果authId为空，表示用户未登录，注册新用户
                auth = Auth.builder()
                        .weibo(uid.toString())
                        .weiboBindDate(new Date())
                        .status(1)
                        .createDate(new Date())
                        .lastDate(new Date())
                        .build();

                authMapper.insert(auth);
            }

            //保存微博个人信息
            WbInfo wbInfo = WbInfo.builder()
                    .authId(auth.getId())
                    .uid(uid.toString())
                    .accessToken(accessToken.toString())
                    .accessTokenDate(new Date())
                    .expiresIn(expiresIn)
                    .createDate(new Date())
                    .updateDate(new Date())
                    .build();

            wbInfoMapper.insert(wbInfo);

        }


        AuthDTO authDTO = new AuthDTO();
        BeanUtils.copyProperties(auth, authDTO);

        String token = JWTUtil.generateJsonWebToken(auth.getId());
        authDTO.setToken(token);

        //把token保存到redis中，用于注销功能
        redisUtil.set(CacheKey.getJwtToken(auth.getId()), token, CacheKey.TOKENEXPIRETIME);

        return Result.success("微博登录成功", authDTO);

    }

    /**
     * 根据id修改用户认证信息
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result updateAuth(UserRegisterRequest request) {

        Auth auth = authMapper.selectById(request.getAuthId());

        QueryWrapper<Auth> wrapper = new QueryWrapper<>();

        String userName = request.getUserName();
        //修改用户名
        if (StringUtils.isNotBlank(userName)) {
            wrapper.eq("user_name", userName);

            Integer count = authMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BizException(BizCodeEnum.USER_EXISTS);
            }

            //修改用户名
            auth.setUserName(userName);
        }

        String email = request.getEmail();
        String code = request.getCode();
        //修改邮箱，需要使用邮箱验证码
        if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(code)) {

            //邮箱验证码缓存key
            String cachekey = String.format(CacheKey.CHECK_CODE_KEY, SendCodeEnum.USER_REGISTER, email);

            //获取redis中的验证码
            String cacheCode = redisUtil.get(cachekey);
            //判断用户传递的code验证码是否正确
            if (!request.getCode().equals(cacheCode)) {
                throw new BizException(BizCodeEnum.CODE_ERROR);
            }

            wrapper.eq("email", email);
            Integer count = authMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BizException(BizCodeEnum.USER_EMAIL_EXISTS);
            }

            //修改邮箱
            auth.setEmail(email);
            auth.setEmailBindDate(new Date());
        }

        //修改手机号,需要使用手机号验证码
        String mobile = request.getMobile();
        if (StringUtils.isNotBlank(mobile) && StringUtils.isNotBlank(code)) {

            //手机号验证码缓存key
            String cachekey = String.format(CacheKey.CHECK_CODE_KEY, SendCodeEnum.USER_REGISTER, mobile);

            //校验验证码
            String cacheCode = redisUtil.get(cachekey);
            if (!request.getCode().equals(cacheCode)) {
                throw new BizException(BizCodeEnum.CODE_ERROR);
            }

            wrapper.eq("mobile", mobile);
            Integer count = authMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BizException(BizCodeEnum.USER_MOBILE_EXISTS);
            }

            //修改手机号
            auth.setMobile(mobile);
            auth.setMobileBindDate(new Date());
        }

        int result = authMapper.updateById(auth);
        //执行修改
        if (result > 0) {
            return Result.success("用户认证信息修改成功", auth);
        }

        return Result.error("用户认证信息修改失败");
    }

    /**
     * 根据Email邮箱验证码修改密码
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result pwdByEmail(UserLoginRequest request) {

        String email = request.getEmail();
        //邮箱验证码缓存key
        String cachekey = String.format(CacheKey.CHECK_CODE_KEY, SendCodeEnum.USER_REGISTER, email);

        //获取redis中的验证码
        String cacheCode = redisUtil.get(cachekey);
        //判断用户传递的code验证码是否正确
        if (!request.getCode().equals(cacheCode)) {
            throw new BizException(BizCodeEnum.CODE_ERROR);
        }

        //根据email邮箱查询用户
        QueryWrapper<Auth> wrapper = new QueryWrapper<>();
        wrapper.eq(request.getEmail() != null, "email", request.getEmail());
        Auth auth = authMapper.selectOne(wrapper);

        //修改密码
        String password = MacUtil.makeHashPassword(request.getPassword());
        auth.setPassword(password);
        authMapper.updateById(auth);

        //修改密码后，需要让当前的token失效
        redisUtil.del(CacheKey.getJwtToken(auth.getId()));

        return Result.success("密码修改成功", auth.getId());
    }

    /**
     * 根据手机号验证码修改密码
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result pwdByMobile(UserLoginRequest request) {

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

        //修改密码
        String password = MacUtil.makeHashPassword(request.getPassword());
        auth.setPassword(password);
        authMapper.updateById(auth);

        //修改密码后，需要让当前的token失效
        redisUtil.del(CacheKey.getJwtToken(auth.getId()));

        return Result.success("密码修改成功", auth.getId());
    }

    /**
     * 根据旧密码修改密码
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result pwdByOld(UserLoginRequest request) {

        //获取图片验证码
        String key = request.getKey();
        String code = request.getCode();

        //校验图片验证码,使用图片验证码，确保是人进行的访问
        String cacheCode = redisUtil.get(key);
        if (code == null || !code.equals(cacheCode)) {
            throw new BizException(BizCodeEnum.CODE_ERROR);
        }

        //根据用户ID查询用户信息
        Auth auth = authMapper.selectById(request.getAuthId());

        //请求传过来的旧密码，需要加密比对
        String oldPassword = MacUtil.makeHashPassword(request.getOldPassword());
        //判断旧密码是否错误
        if (auth == null || !auth.getPassword().equals(oldPassword)) {
            throw new BizException(BizCodeEnum.ACCOUNT_PWD_ERROR);
        }

        //修改密码
        String password = MacUtil.makeHashPassword(request.getPassword());
        auth.setPassword(password);
        authMapper.updateById(auth);

        //修改密码后，需要让当前的token失效
        redisUtil.del(CacheKey.getJwtToken(auth.getId()));

        return Result.success("密码修改成功", auth.getId());
    }


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @Override
    @LoginRec(status = LoginStatus.OUT, note = "用户注销")
    public Result logout(UserLoginRequest request) {
        redisUtil.del(CacheKey.getJwtToken(request.getAuthId()));
        return Result.success("用户注销成功", request.getAuthId());
    }

    /**
     * 根据认证信息主键ID解绑微信
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result unbindWx(UserLoginRequest request) {

        Auth auth = authMapper.selectById(request.getAuthId());
        if (auth == null) {
            return Result.error("用户不存在");
        }

        //更新用户微信相关信息
        auth.setWeixin(null);
        auth.setWeixinBindDate(null);
        authMapper.updateById(auth);

        //删除微信个人信息
        wxInfoMapper.deleteById(request.getAuthId());

        return Result.success("解绑微信成功");
    }

    /**
     * 刷新用户个人信息(调用微信接口查询)
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result refreshWxInfo(UserLoginRequest request) {
        //获取微信个人信息，根据authId查询数据库
        WxInfo wxInfo = wxInfoMapper.selectById(request.getAuthId());

        //如果结果为空，表示用户未绑定微信
        if (wxInfo == null) {
            return Result.error("用户未绑定微信号");
        }

        //获取微信接口调用凭证 access_token
        Result wxToken = refreshWxToken(request.getAuthId());

        //判断凭证是否正常获取,接口调用成功状态码为200
        if (wxToken.getCode() != ResultCode.OK) {
            return wxToken;
        }

        //access_token能够正常获取，就调用微信接口查询个人微信信息
        String url = wxConfig.getWxInfoUrl()
                + "?access_token=" + wxToken.getData()
                + "&openid=" + wxInfo.getOpenid();

        Map<String, Object> resultMap = HttpUtil.sendGet(url);
        Object openid = resultMap.get("openid");

        //判断查询结果是否正确，根据openId判断
        if (null == openid || !wxInfo.getOpenid().equals(openid.toString())) {
            return Result.error("查询微信个人信息错误");
        }

        //保存微信个人信息
        wxInfo.setNickname(resultMap.get("nickname").toString());
        wxInfo.setSex(resultMap.get("sex").toString());
        wxInfo.setProvince(resultMap.get("province").toString());
        wxInfo.setCity(resultMap.get("city").toString());
        wxInfo.setCountry(resultMap.get("country").toString());
        wxInfo.setHeadimgurl(resultMap.get("headimgurl").toString());
        wxInfo.setUpdateDate(new Date());

        wxInfoMapper.updateById(wxInfo);

        return Result.success("获取最新微信个人信息成功", wxInfo);
    }

    /**
     * 查询用户微信个人信息(从数据库查询)
     *
     * @param request
     * @return
     */
    @Override
    public Result queryWxInfo(UserLoginRequest request) {
        WxInfo wxInfo = wxInfoMapper.selectById(request.getAuthId());

        if (wxInfo == null) {
            return Result.error("用户未绑定微信号");
        }

        return Result.success("用户微信个人信息查询成功", wxInfo);
    }

    /**
     * 根据认证信息主键刷新接口调用凭证access_token
     *
     * @param authId
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public Result refreshWxToken(String authId) {
        //根据id查询微信信息
        WxInfo wxInfo = wxInfoMapper.selectById(authId);

        //判断查询结果是否为空
        if (null == wxInfo) {
            return Result.error("用户没有绑定微信号");
        }

        //判断当前的access_token有效时间是否小于7000秒
        Instant accessTokenTime = wxInfo.getAccessTokenDate().toInstant().plusSeconds(7000);
        //当前时间没有过期
        if (new Date().toInstant().compareTo(accessTokenTime) < 0) {
            //如果小于7000秒，直接返回access_token
            return Result.success("access_token获取成功", wxInfo.getAccessToken());
        }

        //如果access_token已经过期，需要使用refresh_token进行刷新
        //refresh_token有效时间是30天
        Instant refreshTokenTime = wxInfo.getRefreshTokenDate().toInstant().plus(30, ChronoUnit.DAYS);
        //当前时间超过，表示过期了
        if (new Date().toInstant().compareTo(refreshTokenTime) > 0) {
            //如果refresh_token已经过期，返回过期信息
            return Result.error("微信登录过期，需要用户重新登录授权");
        }

        //使用refresh_token获取新的access_token
        String url = wxConfig.getAccessRefreshUrl()
                + "?appid=" + wxConfig.getAppid()
                + "&grant_type=refresh_token"
                + "&refresh_token=" + wxInfo.getRefreshToken();

        Map<String, Object> resultMap = HttpUtil.sendGet(url);
        Object accessToken = resultMap.get("access_token");

        //判断获取到的access_token是否为空
        if (null == accessToken) {
            return Result.error("刷新微信access_token失败");
        }

        //更新access_token
        wxInfo.setAccessToken(accessToken.toString());
        wxInfo.setAccessTokenDate(new Date());
        wxInfoMapper.updateById(wxInfo);

        return Result.success("access_token获取成功", wxInfo.getAccessToken());

    }

    /**
     * 微信扫码登录/注册
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @LoginRec(status = LoginStatus.IN, type = AuthType.WEIXIN, note = "微信扫码登录/注册")
    @Override
    public Result<AuthDTO> loginWX(UserLoginRequest request) {
        //根据code获取access_token 和 openid
        String url = wxConfig.getAccessTokenUrl()
                + "?code=" + request.getCode()
                + "&appid=" + wxConfig.getAppid()
                + "&secret=" + wxConfig.getSecret()
                + "&grant_type=authorization_code";

        Map<String, Object> resultMap = HttpUtil.sendGet(url);
        Object accessToken = resultMap.get("access_token");
        Object openId = resultMap.get("openid");
        Object refreshToken = resultMap.get("refresh_token");
        Object unionId = resultMap.get("unionid");

        //判断access_token 和  openid是否为空
        if (accessToken == null || openId == null) {
            return Result.error("微信扫码失败");
        }

        //根据openid查询用户
        //封装查询条件
        QueryWrapper<Auth> wrapper = new QueryWrapper<>();
        //封装查询条件，不为null作为查询条件
        wrapper.eq(openId != null, "weixin", openId.toString());
        Auth auth = authMapper.selectOne(wrapper);

        //判断用户是否存在
        if (auth != null) {
            //如果用户存在则登录成功,更新用户最后登录时间
            authMapper.updateLastDate(auth.getId());

            //如果用户存在，更新接口调用凭证
            QueryWrapper<WxInfo> wxWrapper = new QueryWrapper<>();
            wxWrapper.eq(openId != null, "openid", openId.toString());
            WxInfo wxInfo = wxInfoMapper.selectOne(wxWrapper);
            if (wxInfo != null) {
                wxInfo.setAccessToken(accessToken.toString());
                wxInfo.setAccessTokenDate(new Date());
                wxInfo.setRefreshToken(refreshToken.toString());
                wxInfo.setRefreshTokenDate(new Date());
                wxInfo.setUpdateDate(new Date());
                wxInfoMapper.updateById(wxInfo);
            }

        } else {
            //如果用户不存在
            //判断authId是否不为空
            if (StringUtils.isNoneBlank(request.getAuthId())) {
                //如果authId不为空，表示用户已经登录，绑定用户账户即可
                auth = authMapper.selectById(request.getAuthId());
                //用户绑定微信
                auth.setWeixin(openId.toString());
                auth.setWeixinBindDate(new Date());
                auth.setLastDate(new Date());

                authMapper.updateById(auth);
            } else {
                //如果authId为空，表示用户未登录，进行用户注册
                auth = Auth.builder()
                        .weixin(openId.toString())
                        .weixinBindDate(new Date())
                        .status(1)
                        .createDate(new Date())
                        .lastDate(new Date())
                        .build();

                authMapper.insert(auth);
            }

            //如果用户不存在，新增接口调用凭证
            WxInfo wxInfo = WxInfo.builder()
                    .authId(auth.getId())
                    .openid(openId.toString())
                    .unionid(unionId.toString())
                    .accessToken(accessToken.toString())
                    .accessTokenDate(new Date())
                    .refreshToken(refreshToken.toString())
                    .refreshTokenDate(new Date())
                    .createDate(new Date())
                    .updateDate(new Date())
                    .build();

            wxInfoMapper.insert(wxInfo);
        }

        AuthDTO authDTO = new AuthDTO();
        BeanUtils.copyProperties(auth, authDTO);

        String token = JWTUtil.generateJsonWebToken(auth.getId());
        authDTO.setToken(token);

        //把token保存到redis中，用于注销功能
        redisUtil.set(CacheKey.getJwtToken(auth.getId()), token, CacheKey.TOKENEXPIRETIME);

        return Result.success("微信登录成功", authDTO);
    }

    /**
     * 用户登录-手机验证码登录
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @LoginRec(status = LoginStatus.IN, note = "手机验证码登录")
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

        //把token保存到redis中，用于注销功能
        redisUtil.set(CacheKey.getJwtToken(auth.getId()), token, CacheKey.TOKENEXPIRETIME);


        return Result.success("账户登录成功", authDTO);
    }

    /**
     * 用户登录-用户账户密码登录
     *
     * @param request
     * @return
     */
    @LoginRec(status = LoginStatus.IN, note = "用户账户密码登录")
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

        //把token保存到redis中，用于注销功能
        redisUtil.set(CacheKey.getJwtToken(auth.getId()), token, CacheKey.TOKENEXPIRETIME);

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

        //把token保存到redis中，用于注销功能
        redisUtil.set(CacheKey.getJwtToken(auth.getId()), token, CacheKey.TOKENEXPIRETIME);

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

        //把token保存到redis中，用于注销功能
        redisUtil.set(CacheKey.getJwtToken(auth.getId()), token, CacheKey.TOKENEXPIRETIME);

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

        //把token保存到redis中，用于注销功能
        redisUtil.set(CacheKey.getJwtToken(auth.getId()), token, CacheKey.TOKENEXPIRETIME);

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
