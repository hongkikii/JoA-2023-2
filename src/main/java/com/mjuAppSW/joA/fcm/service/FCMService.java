package com.mjuAppSW.joA.fcm.service;

import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.mjuAppSW.joA.fcm.dto.request.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FCMService {
    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
            .setTitle(request.getTitle())
            .setBody(request.getBody())
            .build();

        Message message = Message.builder()
            .setToken(request.getTargetUserToken())
            .setNotification(notification)
            .setApnsConfig(ApnsConfig.builder()
                .setAps(Aps.builder().setSound("default").build())
                .build())
            .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
