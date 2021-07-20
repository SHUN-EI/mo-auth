package com.mo.model;

import com.mo.enums.BizCodeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by mo on 2021/7/13
 * 返回结果通用封装
 */
@Accessors(chain = true)//给set方法设置返回对象，返回对象就是自己本身
@Data
@Builder//使对象可以使用builder方式进行创建
public class Result<T> {

    /**
     * 响应状态码
     */
    private int code;
    /**
     * 响应信息
     */
    private String msg;
    /**
     * 返回的数据
     */
    private T data;

    /**
     * 成功，传入操作信息
     *
     * @param msg
     * @return
     */
    public static Result success(String msg) {

        return Result.builder()
                .code(ResultCode.OK)
                .msg(msg)
                .data(null)
                .build();
    }

    /**
     * 成功，传入操作信息、数据
     *
     * @param msg
     * @param data
     * @return
     */
    public static Result success(String msg, Object data) {

        return Result.builder()
                .code(ResultCode.OK)
                .msg(msg)
                .data(data)
                .build();
    }

    /**
     * 成功，传入状态码、操作信息、数据
     *
     * @param code
     * @param msg
     * @param data
     * @return
     */
    public static Result success(int code, String msg, Object data) {
        return Result.builder()
                .code(code)
                .msg(msg)
                .data(data)
                .build();
    }


    /**
     * 失败，传入描述信息
     *
     * @param msg
     * @return
     */
    public static Result error(String msg) {
        return Result.builder()
                .code(ResultCode.ERROR)
                .msg(msg)
                .build();
    }


    /**
     * 自定义状态码和提示信息
     *
     * @param code
     * @param msg
     * @return
     */
    public static Result buildCodeAndMsg(int code, String msg) {
        return Result.builder()
                .code(code)
                .msg(msg)
                .build();
    }

    /**
     * 传入枚举，返回信息
     *
     * @param bizCodeEnum
     * @return
     */
    public static Result buildResult(BizCodeEnum bizCodeEnum) {
        return buildCodeAndMsg(bizCodeEnum.getCode(), bizCodeEnum.getMessage());
    }

}
