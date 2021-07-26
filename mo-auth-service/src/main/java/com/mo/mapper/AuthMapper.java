package com.mo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mo.entity.Auth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * Created by mo on 2021/7/15
 * 用户认证信息  Mapper接口
 */
@Mapper
public interface AuthMapper extends BaseMapper<Auth> {


    /**
     * 根据id修改last_date为最新时间
     *
     * @param id
     */
    @Update("UPDATE tb_auth SET last_date=NOW() WHERE id=#{id}")
    void updateLastDate(String id);
}
