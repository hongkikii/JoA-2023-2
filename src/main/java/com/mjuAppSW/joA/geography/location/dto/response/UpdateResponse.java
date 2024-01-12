package com.mjuAppSW.joA.geography.location.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "사용자 위치 업데이트 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateResponse {
    private final Boolean isContained;

    public static UpdateResponse of(Boolean isContained) {
        return UpdateResponse.builder()
                .isContained(isContained)
                .build();
    }
}
