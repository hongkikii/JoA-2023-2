package com.mjuAppSW.joA.domain.room.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class RoomCheckRequest {
    @JsonProperty("roomId")
    @NotNull
    private long roomId;
}
