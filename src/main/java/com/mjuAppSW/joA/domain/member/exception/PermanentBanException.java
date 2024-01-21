package com.mjuAppSW.joA.domain.member.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class PermanentBanException extends BusinessException {

    public PermanentBanException() {
        super(ErrorCode.PERMANENT_BAN);
    }
}
