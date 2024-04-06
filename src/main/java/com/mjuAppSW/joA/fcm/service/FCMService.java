package com.mjuAppSW.joA.fcm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.mjuAppSW.joA.fcm.vo.FCMInfoVO;
import com.mjuAppSW.joA.fcm.vo.FCMMessageVO;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
@Slf4j
@Service
public class FCMService {
    @Value(value = "${fcm.url}")
    private String url;

    @Value(value = "${fcm.json.path}")
    private String jsonPath;

    private final ObjectMapper objectMapper;

    @Async
    public void send(FCMInfoVO vo) {
        try {
            String message = make(vo);
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();
            Response response = client.newCall(request).execute();
            log.info(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String make(FCMInfoVO vo) throws JsonProcessingException {
        FCMMessageVO fcmMessageVO = FCMMessageVO.builder()
            .message(Message.builder()
                .setToken(vo.getTargetMember().getFcmToken())
                .setNotification(Notification.builder()
                    .setTitle(vo.getMemberName() + " " + vo.getConstants().getTitle())
                    .setBody(vo.getConstants().getBody() + vo.getContent())
                    .build())
                .setApnsConfig(ApnsConfig.builder()
                    .setAps(Aps.builder()
                        .setSound("default")
                        .build())
                    .build())
                .build())
            .validateOnly(false)
            .build();
        return objectMapper.writeValueAsString(fcmMessageVO);
    }

    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
            .fromStream(new ClassPathResource(jsonPath).getInputStream())
            .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
