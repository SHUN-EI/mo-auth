package com.mo.wrapper;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.InputStream;

/**
 * Created by mo on 2021/8/14
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    //获取策略文件，直接使用jar中自带的ebay策略文件
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
            System.out.println("使用AntiSamy进行过滤清理，过滤清理之前的数据："+values[i]);
            //进行过滤
            newValues[i] = xssClean(values[i]);
            //过滤后的数据
            System.out.println("使用AntiSamy进行过滤清理，过滤清理之后的数据：" + newValues[i]);
        }

        //返回过滤后的结果
        return newValues;
    }




}
