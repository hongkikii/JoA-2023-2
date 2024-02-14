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
    @Schema(description = "현재 학교 안에 위치하고 있는지 여부(학교 안에 위치할 시 true 반환)")
    private final Boolean isContained;

    public static UpdateResponse of(Boolean isContained) {
        return UpdateResponse.builder()
                .isContained(isContained)
                .build();
    }
}
