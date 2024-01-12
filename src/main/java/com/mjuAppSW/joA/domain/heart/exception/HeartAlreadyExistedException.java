package com.mjuAppSW.joA.domain.heart.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class HeartAlreadyExistedException extends BusinessException {

    public HeartAlreadyExistedException() {
        super(ErrorCode.HEART_ALREADY_EXISTED);
    }
}
