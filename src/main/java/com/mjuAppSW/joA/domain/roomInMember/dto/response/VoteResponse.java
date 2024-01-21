package com.mjuAppSW.joA.domain.roomInMember.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "방 연장 투표 후 상대방의 투표 유무 Response")
public class VoteResponse {
    private Long roomId;
    private Long memberId;
    @Schema(description = "상대방의 result 결과 0 -> 찬성 / 1 -> 반대 혹은 투표하지 않음")
    private String result;

    @Builder
    public VoteResponse(Long roomId, Long memberId, String result) {
        this.roomId = roomId;
        this.memberId = memberId;
        this.result = result;
    }

    public static VoteResponse of(Long roomId, Long memberId, String result) {
        return VoteResponse.builder()
            .roomId(roomId)
            .memberId(memberId)
            .result(result)
            .build();
    }
}