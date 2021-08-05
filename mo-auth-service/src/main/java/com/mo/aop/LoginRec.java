package com.mo.aop;

import com.mo.enums.AuthType;
import com.mo.enums.LoginStatus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by mo on 2021/8/5
 * 登录操作日志注解
 */
//注释使用的目标位置，METHOD用在方法上
@Target(METHOD)
//注解在哪个阶段执行
@Retention(RUNTIME)
@Documented
public @interface LoginRec {

    /**
     * 登录状态: 登录或者注销
     *
     * @return
     */
    LoginStatus status();

    /**
     * 登录类型
     *
     * @return
     */
    AuthType type() default AuthType.UNKNOWN;

    /**
     * 操作说明
     *
     * @return
     */
    String note() default "";

}
