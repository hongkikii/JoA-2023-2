package com.mjuAppSW.joA.domain.roomInMember.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;

@Getter
@Schema(description = "채팅방 퇴장 Request")
public class UpdateExpiredRequest {
    @NotNull
    private Long roomId;
    @NotNull
    private Long memberId;
    @NotNull
    private String expired;
}
