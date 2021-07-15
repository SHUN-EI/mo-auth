package com.mo.request;

import lombok.Data;

/**
 * Created by mo on 2021/7/15
 */
@Data
public class AuthListRequest {

    /**
     * 第几页
     */
    private Integer pageNum;
    /**
     * 每页显示多少条
     */
    private Integer pageSize;
}
