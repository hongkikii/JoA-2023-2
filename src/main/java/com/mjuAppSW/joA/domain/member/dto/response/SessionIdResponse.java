package com.mjuAppSW.joA.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "인증번호 전송 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionIdResponse {
    private final Long id;

    public static SessionIdResponse of(Long id) {
        return SessionIdResponse.builder()
                                        .id(id)
                                        .build();
    }
 }
