package com.mjuAppSW.joA.common.websocket.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class MemberSessionListNullException extends BusinessException {
	public MemberSessionListNullException(){
		super(ErrorCode.MEMBER_SESSION_LIST_IS_NULL);
	}
}
