package com.mjuAppSW.joA.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "로그인 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class LoginRequest {
    @NotBlank
    private final String loginId;
    @NotBlank
    private final String password;
    @NotBlank
    private final String fcmToken;
}
