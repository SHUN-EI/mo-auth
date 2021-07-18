package com.mo.exception;

import com.mo.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by mo on 2021/7/18
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handle(Exception e) {

        //是否为自定义异常
        if (e instanceof BizException) {
            BizException bizException = (BizException) e;
            log.info("[业务异常]{}", e);
            //返回前端的错误信息
            return Result.error(bizException.getCode(), bizException.getMsg());
        } else {
            log.info("[系统异常]{}", e);
            return Result.error("全局异常，未知错误");
        }
    }
}
