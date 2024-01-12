package com.mjuAppSW.joA.domain.member.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class InvalidLoginIdException extends BusinessException {

    public InvalidLoginIdException() {
        super(ErrorCode.INVALID_LOGIN_ID);
    }
}
