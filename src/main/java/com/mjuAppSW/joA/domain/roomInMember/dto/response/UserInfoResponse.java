package com.mjuAppSW.joA.domain.roomInMember.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoResponse {
	private String name;
	private String urlCode;
	private String bio;

	@Builder
	public UserInfoResponse(String name, String urlCode, String bio) {
		this.name = name;
		this.urlCode = urlCode;
		this.bio = bio;
	}

	public static UserInfoResponse of(String name, String urlCode, String bio) {
		return UserInfoResponse.builder()
			.name(name)
			.urlCode(urlCode)
			.bio(bio)
			.build();
	}
}
