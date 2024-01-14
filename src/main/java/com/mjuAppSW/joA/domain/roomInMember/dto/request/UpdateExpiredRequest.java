package com.mjuAppSW.joA.domain.roomInMember.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;

@Getter
public class UpdateExpiredRequest {
    @NotNull
    private Long roomId;
    @NotNull
    private Long memberId;
    @NotNull
    private String expired;
}
