package com.mo.service;

import com.mo.enums.SendCodeEnum;
import com.mo.model.Result;

/**
 * Created by mo on 2021/7/20
 */
public interface NotifyService {

    /**
     * 发送验证码
     *
     * @param sendCodeEnum
     * @param to
     * @return
     */
    Result sendCode(SendCodeEnum sendCodeEnum, String to);
}
