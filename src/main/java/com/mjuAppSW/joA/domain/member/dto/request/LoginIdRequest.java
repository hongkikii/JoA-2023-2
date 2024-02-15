package com.mjuAppSW.joA.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "아이디 중복 검증 request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class LoginIdRequest {
    @NotNull
    private final Long sessionId;
    @NotBlank
    private final String loginId;
}
