package com.mo.utils;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mo on 2021/8/6
 */
public class HttpUtil {

    /**
     * 发起get请求
     *
     * @param url
     * @return
     */
    public static Map<String, Object> sendGet(String url) {
        Map map = new HashMap();

        //创建HttpClient对象(打开浏览器)
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        try {
            //创建get请求对象(输入访问的网址)
            HttpGet httpGet = new HttpGet(url);

            //发送get请求，执行execute方法(点击回车执行访问)
            response = httpClient.execute(httpGet);

            //处理响应(浏览器展示页面)
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity, "utf-8");
            map = JSON.parseObject(json, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放资源
            try {
                if (response != null) {
                    response.close();
                }
                //http客户端关闭
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return map;
    }
}
