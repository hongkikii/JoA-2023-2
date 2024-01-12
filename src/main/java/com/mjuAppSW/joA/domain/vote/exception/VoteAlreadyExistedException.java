package com.mjuAppSW.joA.domain.vote.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class VoteAlreadyExistedException extends BusinessException {

    public VoteAlreadyExistedException() {
        super(ErrorCode.VOTE_ALREADY_EXISTED);
    }
}
