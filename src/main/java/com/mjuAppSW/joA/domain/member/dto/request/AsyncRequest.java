package com.mjuAppSW.joA.domain.member.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SendRequest {
    private final Long sessionId;
    private final String eMail;

    public static SendRequest of(Long sessionId, String eMail) {
        return SendRequest.builder()
                .sessionId(sessionId)
                .eMail(eMail)
                .build();
    }
}
