package com.mjuAppSW.joA.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "인증번호 확인 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class VerifyCertifyNumRequest {
    @Schema(description = "사용자 세션 id")
    @NotNull
    private final Long id;
    @NotBlank
    private final String certifyNum;
}
