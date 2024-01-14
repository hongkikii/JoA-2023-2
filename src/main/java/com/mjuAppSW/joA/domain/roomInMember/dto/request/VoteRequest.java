package com.mjuAppSW.joA.domain.roomInMember.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VoteRequest {
    @NotNull
    private Long roomId;
    @NotNull
    private Long memberId;
    @NotNull
    private String result;
}
