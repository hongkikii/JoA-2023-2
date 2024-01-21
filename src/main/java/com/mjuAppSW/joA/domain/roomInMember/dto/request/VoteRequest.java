package com.mjuAppSW.joA.domain.roomInMember.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "채팅방 연장 투표 저장 및 상대방 투표 유무 확인 Request")
public class VoteRequest {
    @NotNull
    private Long roomId;
    @NotNull
    private Long memberId;
    @NotNull
    private String result;
}
