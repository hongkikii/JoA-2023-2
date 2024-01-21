package com.mjuAppSW.joA.domain.message.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class FailEncryptException extends BusinessException {
	public FailEncryptException(){
		super(ErrorCode.FAIL_ENCRYPT);
	}
}
