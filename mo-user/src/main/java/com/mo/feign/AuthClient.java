package com.mo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Created by mo on 2021/8/19
 */
@FeignClient("mo-auth-service")
public interface AuthClient {

    //  /auth/wxInfo/query/1253553358925529089
    @GetMapping("/auth/wxInfo/query/{authId}")
    public Map queryWxInfo(@PathVariable("authId") String authId);
}
