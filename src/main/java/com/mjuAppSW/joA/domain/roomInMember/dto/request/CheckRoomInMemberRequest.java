package com.mjuAppSW.joA.domain.roomInMember.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "채팅방 생성 전 채팅방 유무 확인 Request")
public class CheckRoomInMemberRequest {
    @NotNull
    private Long memberId1;
    @NotNull
    private Long memberId2;
}
