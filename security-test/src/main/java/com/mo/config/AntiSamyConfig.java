package com.mo.config;

import com.mo.filter.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mo on 2021/8/14
 */
@Configuration
public class AntiSamyConfig {

    /**
     * 配置xss过滤器
     */
    @Bean
    public FilterRegistrationBean create() {
        FilterRegistrationBean<XssFilter> filterRegistrationBean = new FilterRegistrationBean<>(new XssFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);

        return filterRegistrationBean;
    }
}
