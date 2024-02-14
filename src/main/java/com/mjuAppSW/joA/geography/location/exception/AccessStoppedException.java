package com.mjuAppSW.joA.geography.location.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class AccessStoppedException extends BusinessException {

    public AccessStoppedException() {
        super(ErrorCode.ACCESS_STOPPED);
    }
}
