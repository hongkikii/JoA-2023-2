package com.mjuAppSW.joA.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "회원 가입 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class JoinRequest {
    @NotNull
    private final Long id;
    @NotBlank
    private final String loginId;
    @NotBlank
    private final String name;
    @NotBlank
    private final String password;
}
