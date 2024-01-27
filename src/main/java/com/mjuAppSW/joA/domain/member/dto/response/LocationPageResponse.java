package com.mjuAppSW.joA.domain.member.dto.response;

import com.mjuAppSW.joA.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "주변 사람 목록 화면 사용자 정보 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationPageResponse {
    private final String name;
    private final String urlCode;
    private final String bio;

    public static LocationPageResponse of(Member member) {
        return LocationPageResponse.builder()
                .name(member.getName())
                .urlCode(member.getUrlCode())
                .bio(member.getBio())
                .build();
    }
}
