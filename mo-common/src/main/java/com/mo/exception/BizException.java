package com.mo.exception;

import com.mo.enums.BizCodeEnum;
import lombok.Data;

/**
 * Created by mo on 2021/7/18
 * 全局异常处理
 */
@Data
public class BizException extends RuntimeException {

    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String msg;

    public BizException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BizException(BizCodeEnum bizCodeEnum) {
        super(bizCodeEnum.getMessage());
        this.code = bizCodeEnum.getCode();
        this.msg = bizCodeEnum.getMessage();
    }
}
