package com.mjuAppSW.joA.domain.roomInMember.dto;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.room.Room;
import lombok.Data;

@Data
public class VoteResponse {
    private Room room;
    private Member member;
    private String result;

    public VoteResponse(Room roomId, Member memberId, String result) {
        this.room = roomId;
        this.member = memberId;
        this.result = result;
    }
}
