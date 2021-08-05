package com.mo.enums;

import lombok.Getter;

/**
 * Created by mo on 2021/8/5
 * 操作类型
 */
@Getter
public enum CommandEnum {
    LOGINSUCCESS(1),

    LOGINFAIL(2),

    LOGOUTSUCCESS(3),

    LOGOUTFAIL(4);

    private int command;

    CommandEnum(int command) {
        this.command = command;
    }

    //根据command类型，可以获取到对应的枚举
    public static CommandEnum getCommandEnum(int command) {

        for (CommandEnum commandEnum : CommandEnum.values()) {
            if (commandEnum.getCommand() == command) {
                return commandEnum;
            }
        }

        return LOGINSUCCESS;
    }

}


