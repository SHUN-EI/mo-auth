package com.mo.exception;

import com.mo.model.Result;
import com.mo.model.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * Created by mo on 2021/7/18
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handle(Exception e) {

        StringBuffer sb = new StringBuffer();

        //是否为自定义异常
        if (e instanceof BizException) {
            BizException bizException = (BizException) e;
            log.info("[业务异常]{}", e);
            //返回前端的错误信息
            return Result.error(bizException.getCode(), bizException.getMsg());
        } else if (e instanceof ConstraintViolationException) {
            //参数检验异常
            Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) e).getConstraintViolations();
            constraintViolations.forEach(msg -> sb.append(msg.getMessage()).append(";"));
            log.info("[参数检验异常]{}", e);
            return Result.error(ResultCode.BAD_REQUEST, sb.toString());
        } else {
            log.info("[系统异常]{}", e);
            return Result.error("全局异常，未知错误");
        }
    }

}
