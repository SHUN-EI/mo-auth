package com.mo.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by mo on 2021/7/13
 * 返回分页结果通用包装
 */
@Accessors(chain = true)//给set方法设置返回对象，返回对象就是自己本身
@Data
@Builder//使对象可以使用builder方式进行创建
public class ResultPage<T> {

    /**
     * 响应状态码
     */
    private int code;
    /**
     * 响应信息
     */
    private String msg;
    /**
     * 总记录数
     */
    private Long totalRecord;
    /**
     * 总页数
     */
    private Long totalPage;
    /**
     * 每页的大小，每页多少条
     */
    private Integer pageSize;
    /**
     * 当前页码数
     */
    private Integer pageNum;
    /**
     * 分页结果集
     */
    private List<T> data;


    /**
     * 成功，传入分页信息、数据
     *
     * @param totalRecord
     * @param totalPage
     * @param pageSize
     * @param pageNum
     * @param data
     * @return
     */
    public static ResultPage success(Long totalRecord, Long totalPage, Integer pageSize, Integer pageNum, List data) {

        return ResultPage.builder()
                .code(ResultCode.OK)
                .msg("操作成功")
                .totalRecord(totalRecord)
                .totalPage(totalPage)
                .pageSize(pageSize)
                .pageNum(pageNum)
                .data(data)
                .build();
    }


    /**
     * 成功，传入状态码、响应信息、分页信息、数据
     * @param code
     * @param msg
     * @param totalRecord
     * @param totalPage
     * @param pageSize
     * @param pageNum
     * @param data
     * @return
     */
    public static ResultPage success(int code, String msg, Long totalRecord, Long totalPage, Integer pageSize, Integer pageNum, List data) {

        return ResultPage.builder()
                .code(code)
                .msg(msg)
                .totalRecord(totalRecord)
                .totalPage(totalPage)
                .pageSize(pageSize)
                .pageNum(pageNum)
                .data(data)
                .build();
    }

}
