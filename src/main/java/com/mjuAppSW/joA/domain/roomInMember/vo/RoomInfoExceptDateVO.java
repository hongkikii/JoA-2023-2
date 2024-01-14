package com.mjuAppSW.joA.domain.roomInMember.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RoomInfoExceptDateVO {
    private Long roomId;
    private String name;
    private String urlCode;
    private String content;
    private String unCheckedMessage;

    @Builder
    public RoomInfoExceptDateVO(Long roomId, String name, String urlCode, String content, String unCheckedMessage) {
        this.roomId = roomId;
        this.name = name;
        this.urlCode = urlCode;
        this.content = content;
        this.unCheckedMessage = unCheckedMessage;
    }

    public static RoomInfoExceptDateVO of(Long roomId, String name, String urlCode, String content, String unCheckedMessage) {
        return RoomInfoExceptDateVO.builder()
            .roomId(roomId)
            .name(name)
            .urlCode(urlCode)
            .content(content)
            .unCheckedMessage(unCheckedMessage)
            .build();
    }
}
