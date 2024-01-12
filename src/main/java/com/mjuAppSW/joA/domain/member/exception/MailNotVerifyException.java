package com.mjuAppSW.joA.domain.member.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class MailNotVerifyException extends BusinessException {

    public MailNotVerifyException() {
        super(ErrorCode.MAIL_NOT_VERIFY);
    }
}
