package com.mjuAppSW.joA.domain.roomInMember.vo;

import java.util.Date;

import com.mjuAppSW.joA.domain.room.Room;

public interface RoomInfoIncludeMessageVO {
	Room getRoom();
	Date getDate();
	String getName();
	String getUrlCode();
	String getContent();
}
