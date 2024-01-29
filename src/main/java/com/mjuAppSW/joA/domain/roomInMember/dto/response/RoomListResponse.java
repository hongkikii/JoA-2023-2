package com.mjuAppSW.joA.domain.roomInMember.dto.response;

import java.util.List;

import com.mjuAppSW.joA.domain.roomInMember.vo.RoomInfoExceptDateVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "채팅방 목록 페이지 정보 조회 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomListResponse {
	private final List<RoomInfoExceptDateVO> roomListVOs;

	public static RoomListResponse of(List<RoomInfoExceptDateVO> roomListVOs) {
		return RoomListResponse.builder()
			.roomListVOs(roomListVOs)
			.build();
	}
}