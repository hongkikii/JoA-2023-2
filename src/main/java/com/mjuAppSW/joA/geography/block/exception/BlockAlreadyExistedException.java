package com.mjuAppSW.joA.geography.block.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class BlockAlreadyExistedException extends BusinessException {

    public BlockAlreadyExistedException() {
        super(ErrorCode.BLOCK_ALREADY_EXISTED);
    }
}
