package com.mjuAppSW.joA.domain.member.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class InvalidS3Exception extends BusinessException {

    public InvalidS3Exception() {
        super(ErrorCode.INVALID_S3);
    }
}
