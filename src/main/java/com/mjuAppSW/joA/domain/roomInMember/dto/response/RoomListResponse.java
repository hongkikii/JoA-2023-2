package com.mjuAppSW.joA.domain.roomInMember.dto.response;

import java.util.List;

import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoExceptDateVO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RoomListResponse {
	List<RoomInfoExceptDateVO> roomListVOs;

	@Builder
	public RoomListResponse(List<RoomInfoExceptDateVO> roomListVOs){
		this.roomListVOs = roomListVOs;
	}

	public static RoomListResponse of(List<RoomInfoExceptDateVO> roomListVOs) {
		return RoomListResponse.builder()
			.roomListVOs(roomListVOs)
			.build();
	}
}