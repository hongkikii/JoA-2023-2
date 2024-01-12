package com.mjuAppSW.joA.domain.roomInMember.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class updateExpiredRequest {
    @JsonProperty("roomId")
    @NotNull
    private long roomId;
    @JsonProperty("memberId")
    @NotNull
    private long memberId;
    @JsonProperty("expired")
    @NotNull
    private String expired;
}
