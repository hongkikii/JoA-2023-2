package com.mjuAppSW.joA.domain.message.vo;

import lombok.Data;

@Data
public class MessageVO {
    private String content;
    public MessageVO(String content){
        this.content = content;
    }
}
