package com.mjuAppSW.joA.domain.messageReport.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.domain.message.entity.Message;
import com.mjuAppSW.joA.domain.messageReport.entity.MessageReport;
import com.mjuAppSW.joA.domain.messageReport.repository.MessageReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageReportQueryService {
	private final MessageReportRepository messageReportRepository;

	public MessageReport getById(Long messageReportId){
		return messageReportRepository.findById(messageReportId)
			.orElseThrow(() -> BusinessException.MessageReportNotFoundException);
	}

	public void validateNoExistedMessageReport(Message message){
		messageReportRepository.findByMessage(message)
			.ifPresent(messageReport -> {
				throw BusinessException.MessageReportAlreadyExistedException;
			});
	}
}
