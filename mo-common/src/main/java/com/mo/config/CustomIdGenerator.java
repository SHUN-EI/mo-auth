package com.mo.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.mo.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * Created by mo on 2021/7/22
 * 自定义ID生成器
 */
public class CustomIdGenerator implements IdentifierGenerator {


    @Bean
    public IdWorker createIdWorker() {
        return new IdWorker(1, 1);
    }

    @Autowired
    private IdWorker idWorker;

    @Override
    public Number nextId(Object entity) {
        return idWorker.nextId();
    }
}
