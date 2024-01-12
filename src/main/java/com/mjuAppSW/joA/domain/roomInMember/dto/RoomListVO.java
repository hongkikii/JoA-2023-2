package com.mjuAppSW.joA.domain.roomInMember.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoomListVO {
    List<RoomListDTO> roomDTOList;
    private String status;

    public RoomListVO(List roomDTOList, String status){
        this.roomDTOList = roomDTOList;
        this.status = status;
    }
}
