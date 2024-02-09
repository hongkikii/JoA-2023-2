package com.mjuAppSW.joA.domain.vote.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class VoteNotFoundException extends BusinessException {

    public VoteNotFoundException() {
        super(ErrorCode.VOTE_NOT_FOUND);
    }
}
