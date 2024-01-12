package com.mjuAppSW.joA.domain.memberProfile.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class AccessForbiddenException extends BusinessException {

    public AccessForbiddenException() {
        super(ErrorCode.ACCESS_FORBIDDEN);
    }
}
