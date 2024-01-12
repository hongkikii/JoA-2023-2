package com.mjuAppSW.joA.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "비밀번호 변경 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class TransPasswordRequest {
    @NotNull
    private final Long id;
    @NotBlank
    private final String currentPassword;
    @NotBlank
    private final String newPassword;
}
