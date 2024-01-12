package com.mjuAppSW.joA.domain.memberProfile.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "한 줄 소개 변경 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class BioRequest {
    @NotNull
    private final Long id;
    @NotBlank
    private final String bio;
}
