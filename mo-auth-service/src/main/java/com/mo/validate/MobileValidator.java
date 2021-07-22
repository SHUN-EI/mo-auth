package com.mo.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by mo on 2021/7/19
 * 手机号参数检验注解-实现类
 * <p>
 * 校验注解类，必须实现ConstraintValidator接口
 * 接口的第一个泛型是手机号参数校验的注解，第二个泛型是校验参数的类型
 */
public class MobileValidator implements ConstraintValidator<Mobile, String> {

    private String regexp;

    /**
     * 初始化方法
     *
     * @param constraintAnnotation
     */
    @Override
    public void initialize(Mobile constraintAnnotation) {
        //获取校验的正则表达式
        this.regexp = constraintAnnotation.regexp();
    }

    /**
     * 校验方法
     * 校验，返回true表示通过校验，返回false表示校验失败，错误信息为注解中的message
     * 第一个value参数是@Mobile注解所注解使用的字段值
     *
     * @param value
     * @param context
     * @return
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            //参数为空的时候，直接不校验，直接通过
            return true;
        }

        //利用正则去检验参数
        return value.matches(regexp);
    }
}
