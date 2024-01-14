package com.mjuAppSW.joA.domain.roomInMember.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class RoomInMemberNotFoundException extends BusinessException {
	public RoomInMemberNotFoundException() { super(ErrorCode.RIM_NOT_FOUND);}
}
