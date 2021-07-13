package com.mo.enums;

import lombok.Getter;

/**
 * Created by mo on 2021/7/13
 * 用户账号状态
 */
public enum AccountStatus {

    TRIAL(0, "试用账号"),
    OFFICIAL(1, "正常账号"),
    FORBIDDEN(-1, "禁用账号");

    @Getter
    private int status;
    @Getter
    private String desc;

    AccountStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
