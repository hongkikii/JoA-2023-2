package com.mjuAppSW.joA.domain.roomInMember.dto;

import lombok.Data;

@Data
public class VoteDTO {
    private long roomId;
    private long memberId;
    private String result;

    public VoteDTO(long roomId, long memberId, String result) {
        this.roomId = roomId;
        this.memberId = memberId;
        this.result = result;
    }
}
