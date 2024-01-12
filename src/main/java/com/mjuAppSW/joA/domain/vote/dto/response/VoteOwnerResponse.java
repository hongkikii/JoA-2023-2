package com.mjuAppSW.joA.domain.vote.dto.response;

import com.mjuAppSW.joA.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "투표 화면 사용자 정보 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VoteOwnerResponse {
    private final String name;
    private final String urlCode;

    public static VoteOwnerResponse of(Member member) {
        return VoteOwnerResponse.builder()
                .name(member.getName())
                .urlCode(member.getUrlCode())
                .build();
    }
}
