package com.mjuAppSW.joA.domain.roomInMember.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class RoomInMemberAlreadyExistedException extends BusinessException {
	public RoomInMemberAlreadyExistedException() { super(ErrorCode.RIM_ALREADY_EXISTED);}
}
