package com.mjuAppSW.joA.domain.roomInMember.dto;

import lombok.Data;

@Data
public class CheckVoteDTO {
    private long roomId;
    private long memberId;
    private String result;

    public CheckVoteDTO(long roomId, long memberId, String result) {
        this.roomId = roomId;
        this.memberId = memberId;
        this.result = result;
    }
}
