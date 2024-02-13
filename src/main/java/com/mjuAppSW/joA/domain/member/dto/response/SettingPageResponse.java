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
    @Schema(description = "S3 객체(이미지) URL 고유 코드, 기본 이미지일 시 빈 문자열 반환")
    private final String urlCode;

    public static SettingPageResponse of(Member member) {
        return SettingPageResponse.builder()
                .name(member.getName())
                .urlCode(member.getUrlCode())
                .build();
    }
}
