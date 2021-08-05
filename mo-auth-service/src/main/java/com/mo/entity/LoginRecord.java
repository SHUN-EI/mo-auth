package com.mo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mo on 2021/8/4
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName("tb_login_record")
public class LoginRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录记录主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 认证信息ID
     */
    private String authId;

    /**
     * 登录方式，1手机，2邮箱，3用户名，4qq，5微信，6新浪微博
     */
    private int type;

    /**
     * 操作类型：1登录成功，2登录失败，3注销成功，4注销失败
     */
    private int command;

    /**
     * 登录ip
     */
    private String ip;

    /**
     * 所在经度
     */
    private String longitude;

    /**
     * 所在纬度
     */
    private String latitude;

    /**
     * 操作说明
     */
    private String note;

    /**
     * 登录时间
     */
    private Date createDate;


}
