package com.mjuAppSW.joA.geography.college.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "학교 범위 등록 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class PolygonRequest {
    @NotNull
    private final Long collegeId;
    @NotNull
    private final Double topLeftLng;
    @NotNull
    private final Double topLeftLat;
    @NotNull
    private final Double topRightLng;
    @NotNull
    private final Double topRightLat;
    @NotNull
    private final Double bottomRightLng;
    @NotNull
    private final Double bottomRightLat;
    @NotNull
    private final Double bottomLeftLng;
    @NotNull
    private final Double bottomLeftLat;
}
