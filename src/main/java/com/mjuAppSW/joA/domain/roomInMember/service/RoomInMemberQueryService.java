package com.mjuAppSW.joA.domain.roomInMember.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjuAppSW.joA.domain.heart.exception.RoomAlreadyExistedException;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.roomInMember.repository.RoomInMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomInMemberQueryService {
	private final RoomInMemberRepository roomInMemberRepository;

	public void validateNoRoom(Member giveMember, Member takeMember){
		if(roomInMemberRepository.checkRoomInMember(giveMember, takeMember).size() != 0){
			throw new RoomAlreadyExistedException();
		}
	}
}
