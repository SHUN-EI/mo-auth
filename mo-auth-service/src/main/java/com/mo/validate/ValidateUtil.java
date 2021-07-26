package com.mo.validate;

import com.mo.enums.AuthType;

/**
 * Created by mo on 2021/7/19
 * 自定义参数验证工具类
 */
public class ValidateUtil {

    /**
     * 手机号校验正则
     */
    public static final String MOBILE_REGX = "^[1][3-9][0-9]{9}$";

    /**
     * 手机号校验错误提示信息
     */
    public static final String MOBILE_MSG = "手机格式错误";

    /**
     * 用户账号校验正则
     */
    public static final String USERNAME_REGX = "^[a-zA-Z]\\w{5,19}$";

    /**
     * 用户账号校验错误提示信息
     */
    public static final String USERNAME_MSG = "账号必须是字母开头，字母，数字，下划线组成，6-20位";


    /**
     * 根据用户输入数据，判断登录类型
     *
     * @param input
     * @return
     */
    public static AuthType chechLogin(String input) {
        //判断是否为手机号
        if (input.matches(MOBILE_REGX)) {
            return AuthType.MOBILE;
        }

        //判断是否为账号
        if (input.matches(USERNAME_REGX)) {
            return AuthType.USERNAME;
        }

        //既不是手机号，也不是用账号，返回邮箱类型
        return AuthType.EMAIL;

    }

}
