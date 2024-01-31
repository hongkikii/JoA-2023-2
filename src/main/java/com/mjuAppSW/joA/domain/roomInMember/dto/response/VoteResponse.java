package com.mjuAppSW.joA.domain.roomInMember.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "채팅방 연장 투표 후 상대방의 투표 유무 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VoteResponse {
    private final Long roomId;
    private final Long memberId;
    @Schema(description = "상대방의 투표 결과 [0 -> 찬성, 1 -> 반대, 2 -> 투표하지 않음]")
    private final String result;

    public static VoteResponse of(Long roomId, Long memberId, String result) {
        return VoteResponse.builder()
            .roomId(roomId)
            .memberId(memberId)
            .result(result)
            .build();
    }
}
