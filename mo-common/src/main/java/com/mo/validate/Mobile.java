package com.mo.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by mo on 2021/7/19
 * 自定义参数检验注解-手机号参数检验注解
 */
//用户具体校验的实现类
@Constraint(validatedBy = MobileValidator.class)
//注解可以使用的地方
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
//注解在什么时候使用
@Retention(RUNTIME)
public @interface Mobile {

    //手机号校验的正则表达式
    String regexp() default ValidateUtil.MOBILE_REGX;

    //手机号校验的错误提示信息
    String message() default ValidateUtil.MOBILE_MSG;


    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        Mobile[] value();
    }
}
