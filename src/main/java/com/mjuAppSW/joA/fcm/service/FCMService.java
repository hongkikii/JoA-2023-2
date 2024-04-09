package com.mjuAppSW.joA.fcm.service;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.mjuAppSW.joA.fcm.vo.FCMInfoVO;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class FCMService {
    private final FirebaseMessaging firebaseMessaging;

    @Async
    public void send(FCMInfoVO vo){
        try{
            Message message = make(vo);
            String response = firebaseMessaging.sendAsync(message).get();
            log.info("Send FCM Notification targetMember = {}, response = {}", vo.getTargetMember().getId(), response);
        }catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }
    }

    public Message make(FCMInfoVO vo){
        log.info(vo.getTargetMember().getFcmToken());
        log.info("title = {}, body = {}", vo.getConstants().getTitle(), vo.getConstants().getBody());
        log.info(vo.getMemberName());
        log.info(vo.getContent());
        Notification notification = Notification.builder()
            .setTitle(vo.getMemberName() + " " + vo.getConstants().getTitle())
            .setBody(vo.getConstants().getBody())
        .build();

        Message message = Message.builder()
            .setToken(vo.getTargetMember().getFcmToken())
            .setNotification(notification)
            .setApnsConfig(
                ApnsConfig.builder()
                    .setAps(Aps.builder()
                        .setSound("default")
                        .build())
                .build())
        .build();

        return message;
    }
}
