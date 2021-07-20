package com.mo.service.impl;

import com.mo.constant.CacheKey;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.SendCodeEnum;
import com.mo.model.Result;
import com.mo.service.MailService;
import com.mo.service.NotifyService;
import com.mo.utils.CheckUtil;
import com.mo.utils.CommonUtil;
import com.mo.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by mo on 2021/7/20
 */
@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private MailService mailService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 验证码的标题
     */
    private static final String SUBJECT = "Mo-auth的验证码";


    /**
     * 验证码的内容
     */
    private static final String CONTENT = "您的验证码是%s,有效时间是5分钟,打死都不要告诉别人哦";

    /**
     * 发送验证码
     *
     * @param sendCodeEnum
     * @param to
     * @return
     */
    @Override
    public Result sendCode(SendCodeEnum sendCodeEnum, String to) {

        String cachekey = String.format(CacheKey.CHECK_CODE_KEY, sendCodeEnum.name(), to);
        //查看缓存里面有没有已发送的验证码
        String cacheValue = redisUtil.get(cachekey);

        //当前时间戳
        Long currentTimestamp = CommonUtil.getCurrentTimestamp();

        //如果cacheValue 不为空，则判断是否60s内重复发送
        if (StringUtils.isNoneBlank(cacheValue)) {
            long ttl = Long.parseLong(cacheValue.split("_")[1]);

            //当前时间戳-验证码发送时间戳，如果小于60秒，则不给重复发送
            if (currentTimestamp - ttl < 60 * 1000) {
                log.info("重复发送验证码,时间间隔:{} 秒", (currentTimestamp - ttl) / 1000);
                return Result.buildResult(BizCodeEnum.CODE_LIMITED);
            }
        }

        //邮箱验证码
        String code = CommonUtil.getRandomCode(6);
        //拼接验证码 eg:886886_3273767673367
        String value = code + "_" + currentTimestamp;
        //把验证码保存到Redis
        redisUtil.set(cachekey, value, CacheKey.CAPTCHAEXPIRE);

        if (CheckUtil.isEmail(to)) {
            //发送邮箱验证码
            mailService.sendMail(to, SUBJECT, String.format(CONTENT, code));
            return Result.success("验证码发送成功");
        } else if (CheckUtil.isPhone(to)) {
            //TODO 短信验证码
        }

        return Result.buildResult(BizCodeEnum.CODE_TO_ERROR);
    }

}
