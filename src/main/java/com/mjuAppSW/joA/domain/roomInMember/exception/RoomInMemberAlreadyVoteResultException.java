package com.mjuAppSW.joA.domain.roomInMember.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class RoomInMemberAlreadyVoteResultException extends BusinessException {
	public RoomInMemberAlreadyVoteResultException(){
		super(ErrorCode.RIM_ALREADY_VOTE_RESULT);
	}
}

