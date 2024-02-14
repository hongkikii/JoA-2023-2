package com.mjuAppSW.joA.domain.member.dto.response;

import com.mjuAppSW.joA.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "투표 화면 사용자 정보 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VotePageResponse {
    private final String name;
    @Schema(description = "S3 저장소 내 프로필 사진 고유 코드", defaultValue = "")
    private final String urlCode;

    public static VotePageResponse of(Member member) {
        return VotePageResponse.builder()
                .name(member.getName())
                .urlCode(member.getUrlCode())
                .build();
    }
}
