package com.mjuAppSW.joA.domain.room.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class OverOneDayException extends BusinessException {
	public OverOneDayException() {
		super(ErrorCode.OVER_ONE_DAY);
	}
}
