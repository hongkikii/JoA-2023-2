package com.mjuAppSW.joA.domain.member.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class InvalidCertifyNumberException extends BusinessException {

    public InvalidCertifyNumberException() {
        super(ErrorCode.INVALID_CERTIFY_NUMBER);
    }
}
