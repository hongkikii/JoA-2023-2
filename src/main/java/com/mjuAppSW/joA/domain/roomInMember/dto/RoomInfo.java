package com.mjuAppSW.joA.domain.roomInMember.dto;

import com.mjuAppSW.joA.domain.room.Room;

import java.util.Date;

public interface RoomInfo {
    Room getRoom();
    Date getDate();
    String getName();
    String getUrlCode();
    String getContent();
}
