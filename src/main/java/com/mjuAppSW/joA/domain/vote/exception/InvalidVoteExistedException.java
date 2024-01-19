package com.mjuAppSW.joA.domain.vote.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class InvalidVoteExistedException extends BusinessException {

    public InvalidVoteExistedException() {
        super(ErrorCode.INVALID_VOTE_EXISTED);
    }
}
