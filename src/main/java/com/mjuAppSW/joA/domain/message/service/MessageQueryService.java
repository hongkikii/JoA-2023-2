package com.mjuAppSW.joA.domain.message.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.domain.message.entity.Message;
import com.mjuAppSW.joA.domain.message.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageQueryService {
	private final MessageRepository messageRepository;

	public Message getById(Long messageId){
		return messageRepository.findById(messageId)
			.orElseThrow(() -> BusinessException.MessageNotFoundException);
	}
}
