package com.mjuAppSW.joA.domain.roomInMember.service;

import com.mjuAppSW.joA.domain.member.vo.UserFcmTokenVO;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.domain.room.entity.Room;
import com.mjuAppSW.joA.domain.roomInMember.entity.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.repository.RoomInMemberRepository;
import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.member.vo.UserInfoVO;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoExceptMessageVO;
import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoIncludeMessageVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomInMemberQueryService {
	private final RoomInMemberRepository roomInMemberRepository;

	public void validateNoRoom(Member giveMember, Member takeMember){
		if(roomInMemberRepository.checkRoomInMember(giveMember, takeMember).size() != 0){
			throw BusinessException.RoomAlreadyExistedException;
		}
	}

	public RoomInMember getByRoomAndMember(Room room, Member member){
		return roomInMemberRepository.findByRoomAndMember(room, member)
			.orElseThrow(() -> BusinessException.RoomInMemberNotFoundException);
	}

	public RoomInMember getOpponentByRoomAndMember(Room room, Member member){
		return roomInMemberRepository.findOpponentByRoomAndMember(room, member)
			.orElseThrow(() -> BusinessException.RoomInMemberNotFoundException);
	}

	public UserInfoVO getOpponentUserInfoByRoomAndMember(Room room, Member member){
		return roomInMemberRepository.findOpponentUserInfoByRoomAndMember(room, member)
			.orElseThrow(() -> BusinessException.MemberNotFoundException);
	}

	public UserFcmTokenVO getOpponentFcmTokenByRoomAndMember(Room room, Member member){
		return roomInMemberRepository.findOpponentFcmTokenByRoomAndMember(room, member)
			.orElseThrow(() -> BusinessException.FcmTokenNotFoundException);
	}

	public RoomInMember getOpponentByRoomAndMemberAndExpired(Room room, Member member, String expired){
		return roomInMemberRepository.findOpponentByRoomAndMemberAndExpired(room, member, expired)
			.orElseThrow(() -> BusinessException.RoomInMemberNotFoundException);
	}

	public RoomInfoExceptMessageVO getExceptMessageByRoomAndMember(Room room, Member member){
		return roomInMemberRepository.findExceptMessageByRoomAndMember(room, member)
			.orElseThrow(() -> BusinessException.RoomInMemberNotFoundException);
	}

	public RoomInfoIncludeMessageVO getIncludeMessageByRoomAndMember(Room room, Member member){
		List<RoomInfoIncludeMessageVO> roomInfoIncludeMessageVOList = roomInMemberRepository.findIncludeMessageByRoomAndMember(room, member);
		if(roomInfoIncludeMessageVOList.isEmpty()){
			throw BusinessException.MessageNotFoundException;
		}
		return roomInfoIncludeMessageVOList.get(0);
	}
}
