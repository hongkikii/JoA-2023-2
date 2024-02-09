package com.mjuAppSW.joA.domain.message.exception;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.common.exception.ErrorCode;

public class MessageReportAlreadyReportException extends BusinessException{
	public MessageReportAlreadyReportException(){
		super(ErrorCode.MESSAGE_REPORT_ALREADY_REPORT);
	}

}
