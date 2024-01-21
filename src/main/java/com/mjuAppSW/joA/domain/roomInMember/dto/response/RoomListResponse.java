package com.mjuAppSW.joA.domain.roomInMember.dto.response;

import java.util.List;

import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoExceptDateVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "채팅방 목록 페이지 정보 조회 Response")
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