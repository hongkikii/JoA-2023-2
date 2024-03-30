package com.mjuAppSW.joA.fcm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "알림 전송 Request")
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class NotificationRequest{
    private String targetUserToken;
    private String title;
    private String body;
}
