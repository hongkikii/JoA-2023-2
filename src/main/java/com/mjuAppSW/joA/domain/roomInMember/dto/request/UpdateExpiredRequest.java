package com.mjuAppSW.joA.domain.roomInMember.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "채팅방 퇴장 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class UpdateExpiredRequest {
    @NotNull
    private final Long roomId;
    @NotNull
    private final Long memberId;
}
