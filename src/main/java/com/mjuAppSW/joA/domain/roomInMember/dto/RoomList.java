package com.mjuAppSW.joA.domain.roomInMember.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoomList {
    List<RoomDTO> roomDTOList;
    private String status;

    public RoomList(List roomDTOList, String status){
        this.roomDTOList = roomDTOList;
        this.status = status;
    }
}
