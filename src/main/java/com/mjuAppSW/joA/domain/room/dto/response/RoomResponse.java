package com.mjuAppSW.joA.domain.room.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "방 생성 후 RoomId Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomResponse {
    private final Long roomId;

    public static RoomResponse of(Long roomId) {
        return RoomResponse.builder()
            .roomId(roomId)
            .build();
    }
}
