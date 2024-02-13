package com.mjuAppSW.joA.domain.heart.dto;

import com.mjuAppSW.joA.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "하트 전송 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HeartResponse {
    @Schema(description = "서로 하트를 전송한지(그래서 매칭된지) 여부")
    private final Boolean isMatched;
    private final String giveName;
    private final String takeName;
    private final String giveUrlCode;
    private final String takeUrlCode;

    public static HeartResponse of(Boolean isMatched, Member giveMember, Member takeMember) {
        return HeartResponse.builder()
                .isMatched(isMatched)
                .giveName(giveMember.getName())
                .takeName(takeMember.getName())
                .giveUrlCode(giveMember.getUrlCode())
                .takeUrlCode(takeMember.getUrlCode())
                .build();
    }
}
