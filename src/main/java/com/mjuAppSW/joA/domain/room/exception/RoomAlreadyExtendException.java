package com.mjuAppSW.joA.domain.room.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class RoomAlreadyExtendException extends BusinessException {
	public RoomAlreadyExtendException(){
		super(ErrorCode.ROOM_ALREADY_EXTEND);
	}
}
