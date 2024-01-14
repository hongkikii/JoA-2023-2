package com.mjuAppSW.joA.domain.report.message.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class MessageReportNotFoundException extends BusinessException {
	public MessageReportNotFoundException(){
		super(ErrorCode.MESSAGE_REPORT_NOT_FOUND);
	}
}
