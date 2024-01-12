package com.mjuAppSW.joA.domain.room.dto;

import lombok.Data;

@Data
public class RoomResponse {
    private Long roomId;

    public RoomResponse(Long roomId){
        this.roomId = roomId;
    }
}
