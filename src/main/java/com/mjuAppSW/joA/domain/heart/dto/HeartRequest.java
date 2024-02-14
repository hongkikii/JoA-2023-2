package com.mjuAppSW.joA.domain.heart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "하트 전송 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class HeartRequest {
    @Schema(description = "하트를 보내는 사용자의 세션 id")
    @NotNull
    private final Long giveId;
    @Schema(description = "하트를 받는 사용자의 pk")
    @NotNull
    private final Long takeId;
    @Schema(description = "실명 여부(실명일 시 true)")
    @NotNull
    private final Boolean named;
}
