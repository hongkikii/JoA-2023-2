package com.mjuAppSW.joA.fcm.controller;

import com.mjuAppSW.joA.fcm.dto.request.NotificationRequest;
import com.mjuAppSW.joA.fcm.service.FCMService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/joa/fcm")
public class FCMApiController {
    private final FCMService fcmService;

    @PostMapping
    public ResponseEntity<Void> sendNotificationByToken(@RequestBody @Valid NotificationRequest request){
        fcmService.sendNotification(request);
        return ResponseEntity.noContent().build();
    }
}
