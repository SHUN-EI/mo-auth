package com.mo.feign;

import com.mo.model.Result;
import com.mo.request.UserLoginRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Created by mo on 2021/8/19
 */
@FeignClient(value = "mo-auth-service")
public interface AuthClient {

    @PostMapping("/api/auth/v1/queryWxInfo")
    Map queryWxInfo(@RequestBody UserLoginRequest request);
}
