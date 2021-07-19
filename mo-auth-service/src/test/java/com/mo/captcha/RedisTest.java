package com.mo.captcha;

import com.mo.AuthApplication;
import com.mo.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by mo on 2021/7/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthApplication.class)
@Slf4j
public class RedisTest {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void redisTest() {

        boolean result = redisUtil.set("test", "hello redis");
        log.info("redis保存是否成功:{}", result);

        String data = redisUtil.get("test");
        log.info("redis保存的数据为:{}", data);

    }
}
