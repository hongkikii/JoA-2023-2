package com.mjuAppSW.joA.domain.member.service.port;

import com.mjuAppSW.joA.domain.member.infrastructure.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {

    private final JavaMailSender javaMailSender;

    @Async
    @Override
    public void send(String email, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);

        message.setSubject("[JoA] " + title + "를 확인하세요.");
        message.setText(title + "는 " + content + " 입니다.");
        javaMailSender.send(message);
    }
}
