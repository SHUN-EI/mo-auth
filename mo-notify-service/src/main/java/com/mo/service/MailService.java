package com.mo.service;

/**
 * Created by mo on 2021/7/20
 */
public interface MailService {

    /**
     * 发送邮件
     *
     * @param to
     * @param subject
     * @param content
     */
    void sendMail(String to, String subject, String content);
}
