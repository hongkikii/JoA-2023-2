package com.mjuAppSW.joA.domain.location.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "사용자 위치 업데이트 request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class UpdateRequest {

    @NotNull
    private final Long id;
    @NotNull
    private final Double latitude;
    @NotNull
    private final Double longitude;
    @NotNull
    private final Double altitude;
}
