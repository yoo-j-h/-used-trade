package com.kh.ct.domain.emp.service;

public interface EmailSender {
    void send(String to, String subject, String text);
}
