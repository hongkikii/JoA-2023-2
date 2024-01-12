package com.mjuAppSW.joA.domain.member.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class JoiningMailException extends BusinessException {

    public JoiningMailException() {
        super(ErrorCode.JOINING_MAIL);
    }
}
