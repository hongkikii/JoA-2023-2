package com.mjuAppSW.joA.domain.roomInMember.dto;

import lombok.Data;

import java.util.Date;

@Data
public class GetRoomDTO {
    public Long roomId;
    public String name;
    public String urlCode;
    public String content;
    public Date time;
    public String unCheckedMessage;

    public GetRoomDTO(Long roomId, String name, String urlCode, String content, Date time, String unCheckedMessage) {
        this.roomId = roomId;
        this.name = name;
        this.urlCode = urlCode;
        this.content = content;
        this.time = time;
        this.unCheckedMessage = unCheckedMessage;
    }
}