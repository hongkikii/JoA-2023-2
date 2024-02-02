package com.mjuAppSW.joA.domain.message.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class FailDecryptException extends BusinessException {
	public FailDecryptException(){
		super(ErrorCode.FAIL_DECRYPT);
	}
}
