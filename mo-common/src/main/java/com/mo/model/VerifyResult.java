package com.mo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by mo on 2021/8/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyResult {

    //是否校验成功
    private boolean isValidate;

    //校验结果信息
    private String msg;

    //认证信息主键,token中携带用户的主键，方便确认用户信息
    private String authId;
}
