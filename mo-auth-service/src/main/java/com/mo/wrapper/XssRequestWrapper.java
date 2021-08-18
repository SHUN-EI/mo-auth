package com.mo.wrapper;

import com.alibaba.fastjson.JSON;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.Map;

/**
 * Created by mo on 2021/8/14
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 获取策略文件，直接使用jar中自带的ebay策略文件
     */
    private static InputStream inputStream = XssRequestWrapper.class.getClassLoader()
            .getResourceAsStream("antisamy-ebay.xml");


    private static Policy policy = null;

    static {
        try {
            //使用静态代码块处理策略对象的创建
            policy = Policy.getInstance(inputStream);
        } catch (PolicyException e) {
            e.printStackTrace();
        }
    }


    /**
     * 使用AntiSamy进行过滤数据
     *
     * @param taintedHtml
     * @return
     */
    private String xssClean(String taintedHtml) {
        String cleanHTML = "";
        try {
            AntiSamy antiSamy = new AntiSamy();
            CleanResults scan = antiSamy.scan(taintedHtml, policy);
            cleanHTML = scan.getCleanHTML();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cleanHTML;
    }

    /**
     * 重写处理请求参数的方法
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);

        //判断参数有值，如果没有值，直接返回
        if (values == null) {
            return null;
        }

        //遍历参数数组，使用AntiSamy进行过滤
        int len = values.length;
        String[] newValues = new String[len];
        for (int i = 0; i < len; i++) {

            //过滤前的数据
            System.out.println("使用AntiSamy进行过滤清理，过滤清理之前的数据：" + values[i]);
            //进行过滤
            newValues[i] = xssClean(values[i]);
            //过滤后的数据
            System.out.println("使用AntiSamy进行过滤清理，过滤清理之后的数据：" + newValues[i]);
        }

        //返回过滤后的结果
        return newValues;
    }

    /**
     * 重写处理json数据的方法
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        //读取流
        BufferedReader reader = new BufferedReader(new InputStreamReader(super.getInputStream(), "UTF-8"));

        //获取json格式的数据
        StringBuilder sb = new StringBuilder();
        String inputStr;
        while ((inputStr = reader.readLine()) != null) {
            sb.append(inputStr);
        }

        //把json转为map
        Map map = JSON.parseObject(sb.toString(), Map.class);

        //过滤前
        System.out.println("过滤前:" + sb.toString());

        //对map中的value值进行AntiSamy的过滤
        map.keySet().forEach(k -> map.put(k, xssClean(map.get(k).toString())));

        //过滤后
        String jsonString = JSON.toJSONString(map);
        System.out.println("过滤后:" + jsonString);

        //把json数据转为流的格式进行返回
        ByteArrayInputStream bais = new ByteArrayInputStream(jsonString.getBytes());

        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };

        return servletInputStream;
    }
}
