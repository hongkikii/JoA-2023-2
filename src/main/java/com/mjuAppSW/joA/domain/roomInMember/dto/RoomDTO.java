package com.mjuAppSW.joA.domain.roomInMember.dto;

import lombok.Data;

@Data
public class RoomDTO {
    private Long roomId;
    private String name;
    private String urlCode;
    private String content;
    private String unCheckedMessage;

    public RoomDTO(Long roomId, String name, String urlCode, String content, String unCheckedMessage) {
        this.roomId = roomId;
        this.name = name;
        this.urlCode = urlCode;
        this.content = content;
        this.unCheckedMessage = unCheckedMessage;
    }
}
