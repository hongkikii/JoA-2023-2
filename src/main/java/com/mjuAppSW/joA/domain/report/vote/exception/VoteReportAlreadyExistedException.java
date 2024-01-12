package com.mjuAppSW.joA.domain.report.vote.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class VoteReportAlreadyExistedException extends BusinessException {

    public VoteReportAlreadyExistedException() {
        super(ErrorCode.VOTE_REPORT_ALREADY_EXISTED);
    }
}
