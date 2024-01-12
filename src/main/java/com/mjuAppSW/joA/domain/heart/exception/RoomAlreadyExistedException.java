package com.mjuAppSW.joA.domain.heart.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class RoomAlreadyExistedException extends BusinessException {

    public RoomAlreadyExistedException() {
        super(ErrorCode.ROOM_EXISTED);
    }
}
