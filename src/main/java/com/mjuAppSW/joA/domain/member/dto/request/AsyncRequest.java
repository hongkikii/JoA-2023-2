package com.mjuAppSW.joA.domain.member.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AsyncRequest {
    private final Long sessionId;
    private final String eMail;

    public static AsyncRequest of(Long sessionId, String eMail) {
        return AsyncRequest.builder()
                .sessionId(sessionId)
                .eMail(eMail)
                .build();
    }
}
