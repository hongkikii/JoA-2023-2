package com.mjuAppSW.joA.domain.room.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjuAppSW.joA.domain.room.entity.Room;
import com.mjuAppSW.joA.domain.room.exception.RoomNotFoundException;
import com.mjuAppSW.joA.domain.room.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomQueryService {
	private final RoomRepository roomRepository;

	public Room getById(Long roomId){
		return roomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);
	}
}
