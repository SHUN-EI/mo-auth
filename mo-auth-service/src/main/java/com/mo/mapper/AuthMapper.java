package com.mo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mo.entity.Auth;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by mo on 2021/7/15
 * 用户认证信息  Mapper接口
 */
@Mapper
public interface AuthMapper extends BaseMapper<Auth> {
}
