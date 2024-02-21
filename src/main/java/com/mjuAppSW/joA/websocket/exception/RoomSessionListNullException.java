package com.mjuAppSW.joA.websocket.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class RoomSessionListNullException extends BusinessException {
	public RoomSessionListNullException(){
		super(ErrorCode.ROOM_SESSION_LIST_IS_NULL);
	}
}
