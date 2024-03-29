package com.mjuAppSW.joA.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "채팅방 입장시 상대방 정보 조회 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChattingPageResponse {
	private final String name;
	@Schema(description = "S3 저장소 내 프로필 사진 고유 코드", defaultValue = "")
	private final String urlCode;
	@Schema(defaultValue = "")
	private final String bio;

	public static ChattingPageResponse of(String name, String urlCode, String bio) {
		return ChattingPageResponse.builder()
			.name(name)
			.urlCode(urlCode)
			.bio(bio)
			.build();
	}
}
