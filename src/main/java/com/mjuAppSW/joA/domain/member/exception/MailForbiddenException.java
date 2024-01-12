package com.mjuAppSW.joA.domain.member.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class MailForbiddenException extends BusinessException {

    public MailForbiddenException() {
        super(ErrorCode.MAIL_FORBIDDEN);
    }
}
