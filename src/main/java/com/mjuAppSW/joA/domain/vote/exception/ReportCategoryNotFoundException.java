package com.mjuAppSW.joA.domain.vote.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class ReportCategoryNotFoundException extends BusinessException {

    public ReportCategoryNotFoundException() {
        super(ErrorCode.REPORT_CATEGORY_NOT_FOUND);
    }
}
