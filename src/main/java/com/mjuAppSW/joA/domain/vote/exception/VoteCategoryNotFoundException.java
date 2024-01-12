package com.mjuAppSW.joA.domain.vote.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class VoteCategoryNotFoundException extends BusinessException {

    public VoteCategoryNotFoundException() {
        super(ErrorCode.VOTE_CATEGORY_NOT_FOUND);
    }
}
