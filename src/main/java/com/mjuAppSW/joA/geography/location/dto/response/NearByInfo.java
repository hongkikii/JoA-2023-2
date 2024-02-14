package com.mjuAppSW.joA.geography.location.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "주변 사람 정보")
@Builder
public class NearByInfo {
    @Schema(description = "사용자 pk")
    private final Long id;
    private final String name;
    @Schema(description = "S3 저장소 내 프로필 사진 고유 코드", defaultValue = "")
    private final String urlCode;
    @Schema(defaultValue = "")
    private final String bio;
    @Schema(description = "오늘 나에게 하트를 눌렀는지 여부(눌렀을 시 true 반환)")
    private final Boolean isLiked;
}
