package com.mjuAppSW.joA.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "인증번호 전송 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class CertifyNumSendRequest {
    @Schema(description = "학교 이메일 아이디(도메인 미포함)")
    @NotBlank
    private final String collegeEmail;
    @NotNull
    private final Long collegeId;
}
