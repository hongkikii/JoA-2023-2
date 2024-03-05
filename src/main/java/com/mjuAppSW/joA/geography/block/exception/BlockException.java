package com.mjuAppSW.joA.geography.block.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class BlockException extends BusinessException{

    public BlockException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static final BlockException BlockAccessForbiddenException = new BlockException(
            ErrorCode.BLOCK_ACCESS_FORBIDDEN);

    public static final BlockException BlockAlreadyExistedException = new BlockException(
            ErrorCode.BLOCK_ALREADY_EXISTED);

    public static final BlockException LocationNotFoundException = new BlockException(
            ErrorCode.LOCATION_NOT_FOUND);

}
