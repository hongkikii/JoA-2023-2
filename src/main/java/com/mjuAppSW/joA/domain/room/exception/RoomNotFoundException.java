package com.mjuAppSW.joA.domain.room.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class RoomNotFoundException extends BusinessException {
	public RoomNotFoundException(){
		super(ErrorCode.ROOM_NOT_FOUND);
	}
}
