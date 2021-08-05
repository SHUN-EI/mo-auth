package com.mo.aop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.dto.AuthDTO;
import com.mo.entity.LoginRecord;
import com.mo.enums.AuthType;
import com.mo.enums.CommandEnum;
import com.mo.enums.LoginStatus;
import com.mo.mapper.LoginRecordMapper;
import com.mo.model.Result;
import com.mo.model.ResultCode;
import com.mo.request.UserLoginRequest;
import com.mo.utils.JWTUtil;
import com.mo.validate.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by mo on 2021/8/5
 * 切面处理类，操作日志异常日志记录处理
 */
@Slf4j
@Aspect //切面的注解
@Component
public class LoginRecAspect {

    @Autowired
    private LoginRecordMapper loginRecordMapper;


    /**
     * 设置操作日志切入点  在哪里进行操作日志的记录
     */
    @Pointcut("@annotation(com.mo.aop.LoginRec)")
    public void loginRecPointCut() {
    }

    /**
     * 每次登录操作后，记录操作信息，保存到登录记录表中
     *
     * @param joinPoint
     * @param keys
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @AfterReturning(value = "loginRecPointCut()", returning = "keys")
    public void saveLoginRec(JoinPoint joinPoint, Object keys) {

        //获取登录操作的执行结果对象 Result
        if (!(keys instanceof Result)) {
            log.error("记录日志，返回结果类型错误");
            return;
        }

        //方法的返回结果对象
        Result result = (Result) keys;

        //从切面中获取请求参数
        UserLoginRequest request = null;

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof UserLoginRequest) {
                request = (UserLoginRequest) arg;
            }
        }

        //创建登录操作日志对象
        LoginRecord record = new LoginRecord();
        record.setCreateDate(new Date());

        //把登陆信息UserLoginRequest复制到登陆日志对象中
        BeanUtils.copyProperties(request, record);
        try {

            //通过反射机制获取切点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            //从方法中，获取注解
            LoginRec loginRecAnnotation = method.getAnnotation(LoginRec.class);

            if (loginRecAnnotation != null) {

                //1.保存登录方式
                //从注解里获取登录状态,登录状态并登录方式已知
                if (loginRecAnnotation.status() == LoginStatus.IN
                        && loginRecAnnotation.type() != AuthType.UNKNOWN) {
                    //直接设置登录方式
                    record.setType(loginRecAnnotation.type().getType());
                } else {
                    //如果是登录状态,但登录方式不确定，需要判断具体的类型是什么
                    AuthType authType = ValidateUtil.chechLogin(request.getInput());
                    record.setType(authType.getType());
                }

                //2.保存操作类型
                if (result.getCode() == ResultCode.OK) {//操作成功
                    //登录成功
                    if (loginRecAnnotation.status() == LoginStatus.IN) {
                        record.setCommand(CommandEnum.LOGINSUCCESS.getCommand());

                        //解析返回的数据，获取authId
                        AuthDTO authDTO = (AuthDTO) result.getData();
                        String authId = JWTUtil.checkJWT(authDTO.getToken()).getAuthId();
                        record.setAuthId(authId);

                    }

                    //注销成功
                    if (loginRecAnnotation.status() == LoginStatus.OUT) {
                        record.setCommand(CommandEnum.LOGOUTSUCCESS.getCommand());
                        //如果是注销成功，返回的data就是authId
                        record.setAuthId(result.getData().toString());
                    }
                } else {//操作失败

                    //登录失败
                    if (loginRecAnnotation.status() == LoginStatus.IN) {
                        record.setCommand(CommandEnum.LOGINFAIL.getCommand());
                    }

                    //注销失败
                    if (loginRecAnnotation.status() == LoginStatus.OUT) {
                        record.setCommand(CommandEnum.LOGOUTFAIL.getCommand());
                    }
                }

                //记录操作信息
                record.setNote(loginRecAnnotation.note());
            }

            //保存操作日志
            loginRecordMapper.insert(record);

        } catch (Exception e) {
            //保存日志记录，不能影响业务逻辑
            e.printStackTrace();
        }

    }
}
