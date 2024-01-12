package com.mjuAppSW.joA.domain.roomInMember.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckRoomInMemberRequest {
    @JsonProperty("memberId1")
    @NotNull
    private Long memberId1;
    @JsonProperty("memberId2")
    @NotNull
    private Long memberId2;
}
