package com.mjuAppSW.joA.geography.location.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class OutOfCollegeException extends BusinessException {

    public OutOfCollegeException() {
        super(ErrorCode.OUT_OF_COLLEGE);
    }
}
