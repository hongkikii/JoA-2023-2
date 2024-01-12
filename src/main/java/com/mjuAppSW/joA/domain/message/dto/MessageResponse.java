package com.mjuAppSW.joA.domain.message.dto;

import lombok.Data;

@Data
public class MessageResponse {
    private String content;

    public MessageResponse(String content){
        this.content = content;
    }
}
