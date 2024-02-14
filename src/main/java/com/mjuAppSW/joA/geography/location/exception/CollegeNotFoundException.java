package com.mjuAppSW.joA.geography.location.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class CollegeNotFoundException extends BusinessException {

    public CollegeNotFoundException() {
        super(ErrorCode.COLLEGE_NOT_FOUND);
    }
}
