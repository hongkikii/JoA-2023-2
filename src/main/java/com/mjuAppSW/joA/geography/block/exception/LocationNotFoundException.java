package com.mjuAppSW.joA.geography.block.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class LocationNotFoundException extends BusinessException {

    public LocationNotFoundException() {
        super(ErrorCode.LOCATION_NOT_FOUND);
    }
}
