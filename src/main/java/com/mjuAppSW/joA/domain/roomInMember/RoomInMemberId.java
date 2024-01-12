package com.mjuAppSW.joA.domain.roomInMember;

import lombok.Data;

import java.io.Serializable;

@Data

public class RoomInMemberId implements Serializable {
    private Long member;
    private Long room;
    @Override
    public int hashCode(){
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        return super.equals(obj);
    }
}