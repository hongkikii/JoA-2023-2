package com.mjuAppSW.joA.domain.message.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class MessageNotFoundException extends BusinessException {
	public MessageNotFoundException() {
		super(ErrorCode.MESSAGE_NOT_FOUND);
	}
}
