package com.mjuAppSW.joA.domain.room.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "방 생성 후 RoomId Response")
public class RoomResponse {
    private Long roomId;

    @Builder
    public RoomResponse(Long roomId){
        this.roomId = roomId;
    }

    public static RoomResponse of(Long roomId) {
        return RoomResponse.builder()
            .roomId(roomId)
            .build();
    }
}
