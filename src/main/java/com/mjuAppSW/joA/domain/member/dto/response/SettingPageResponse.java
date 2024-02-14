package com.mjuAppSW.joA.domain.member.dto.response;

import com.mjuAppSW.joA.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "설정 페이지 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SettingPageResponse {
    private final String name;
    @Schema(description = "S3 저장소 내 프로필 사진 고유 코드", defaultValue = "")
    private final String urlCode;

    public static SettingPageResponse of(Member member) {
        return SettingPageResponse.builder()
                .name(member.getName())
                .urlCode(member.getUrlCode())
                .build();
    }
}
