package com.mjuAppSW.joA.domain.member.infrastructure;

public interface MailSender {

    void send(String email, String title, String content);
}
