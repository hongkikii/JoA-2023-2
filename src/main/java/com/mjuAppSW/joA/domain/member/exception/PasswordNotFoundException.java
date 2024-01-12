package com.mjuAppSW.joA.domain.member.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class PasswordNotFoundException extends BusinessException {

    public PasswordNotFoundException() {
        super(ErrorCode.PASSWORD_NOT_FOUND);
    }
}
