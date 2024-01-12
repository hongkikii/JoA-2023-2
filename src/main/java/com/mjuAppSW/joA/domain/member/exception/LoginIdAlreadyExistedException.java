package com.mjuAppSW.joA.domain.member.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class LoginIdAlreadyExistedException extends BusinessException {

    public LoginIdAlreadyExistedException() {
        super(ErrorCode.LOGIN_ID_ALREADY_EXISTED);
    }
}
