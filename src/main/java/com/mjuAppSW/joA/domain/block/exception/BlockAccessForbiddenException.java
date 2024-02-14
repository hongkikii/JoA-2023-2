package com.mjuAppSW.joA.domain.block.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class BlockAccessForbiddenException extends BusinessException {

    public BlockAccessForbiddenException() {
        super(ErrorCode.BLOCK_ACCESS_FORBIDDEN);
    }
}
