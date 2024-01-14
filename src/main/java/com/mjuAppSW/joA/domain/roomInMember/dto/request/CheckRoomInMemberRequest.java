package com.mjuAppSW.joA.domain.roomInMember.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CheckRoomInMemberRequest {
    @NotNull
    private Long memberId1;
    @NotNull
    private Long memberId2;
}
