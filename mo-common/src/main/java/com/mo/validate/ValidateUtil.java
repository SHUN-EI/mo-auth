package com.mo.validate;

/**
 * Created by mo on 2021/7/19
 * 自定义参数验证工具类
 */
public class ValidateUtil {

    /**
     * 手机号检验正则
     */
    public static final String MOBILE_REGX = "^[1][3-9][0-9]{9}$";

    /**
     * 手机号检验错误提示信息
     */
    public static final String MOBILE_MSG = "手机格式错误";

    /**
     * 用户账号检验正则
     */
    public static final String USERNAME_REGX = "^[a-zA-Z]\\w{5,19}$";

    /**
     * 用户账号检验错误提示信息
     */
    public static final String USERNAME_MSG = "账号必须是字母开头，字母，数字，下划线组成，6-20位";


}
