package com.mo.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mo.constant.CacheKey;
import com.mo.model.VerifyResult;
import com.mo.utils.JWTUtil;
import com.mo.utils.RedisUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mo on 2021/8/2
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 鉴权排除的接口
     */
    private List<String> excludedUrls;
    /**
     * jwt校验密钥
     */
    private String secret;

    /**
     * 进行鉴权
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取token，在请求头的Authorization属性中
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        //对所有的接口都进行鉴权
        VerifyResult verifyResult = JWTUtil.checkJWT(token);
        if (verifyResult.isValidate()) {

            //从redis中查询token
            String redisToken = redisUtil.get(CacheKey.getJwtToken(verifyResult.getAuthId()));

            //判断查询的redis中的token不为空，而且和现在正在使用的token一致，才可以进行登录后的操作
            if (redisToken != null || token.equals(redisToken)) {
                //鉴权成功，获取authId并设置到请求头中
                exchange.getRequest().mutate().headers(h -> h.add("authId", verifyResult.getAuthId()));

                //TODO 重新生成token,若token的失效时间比较短，在这里重新生成token,可以做到无感知续期token

                //返回结果，放行，转发
                return chain.filter(exchange);
            }
        }

        //获取当前请求的url
        String url = exchange.getRequest().getURI().getPath();
        //有些接口不需要鉴权，但是希望能够获取用户登录的信息，可以进行用户行为分析等功能
        //判断，是否是不需要鉴权的接口，不需要鉴权的接扣，鉴权失败也放行
        if (excludedUrls.contains(url)) {
            //不需要鉴权接口直接放行
            return chain.filter(exchange);
        }

        ServerHttpResponse response = exchange.getResponse();

        //提示用户已经注销,合法的token才可以注销
        if (verifyResult.isValidate()) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("code", 401);
            responseData.put("msg", "用户已经注销");
            return responseError(response, responseData);
        }


        //判断token是否为空，就是非法请求
        if (StringUtils.isBlank(token)) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("code", 401);
            responseData.put("msg", "非法请求，token为空");
            return responseError(response, responseData);
        }

        //鉴权失败，直接返回失败信息
        Map responseData = new HashMap<>();
        responseData.put("code", 500);
        responseData.put("msg", verifyResult.getMsg());

        return responseError(response, responseData);
    }

    /**
     * 封装返回信息
     *
     * @param response
     * @param responseData
     * @return
     */
    private Mono<Void> responseError(ServerHttpResponse response, Map responseData) {
        //将信息转为JSON
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data = null;
        try {
            data = objectMapper.writeValueAsBytes(responseData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //输出错误信息到页面
        DataBuffer buffer = response.bufferFactory().wrap(data);
        //设置状态码
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //设置响应头
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 过滤器执行的优先级
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 10000;
    }
}
