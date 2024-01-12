package com.mjuAppSW.joA.domain.roomInMember.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckVoteRequest {
    @JsonProperty("roomId")
    @NotNull
    private long roomId;
}
