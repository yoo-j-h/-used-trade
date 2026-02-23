package com.kh.ct.domain.emp.service;

public interface EmailSender {
    void send(String to, String subject, String text);

    void sendMultipart(String to, String subject, String text, String html);

    void sendHtml(String to, String subject, String html);
}