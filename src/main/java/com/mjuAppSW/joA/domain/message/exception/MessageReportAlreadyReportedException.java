package com.mjuAppSW.joA.domain.message.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class MessageReportAlreadyReportedException extends BusinessException {
	public MessageReportAlreadyReportedException(){
		super(ErrorCode.MESSAGE_REPORT_ALREADY_REPORTED);
	}
}
