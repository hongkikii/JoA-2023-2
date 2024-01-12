package com.mjuAppSW.joA.domain.message.dto;

import lombok.Data;

import java.util.List;

@Data
public class MessageList {
    private List<MessageResponse> messageResponseList;
    private String status;

    public MessageList(List<MessageResponse> messageResponseList, String status){
        this.messageResponseList = messageResponseList;
        this.status = status;
    }
}
