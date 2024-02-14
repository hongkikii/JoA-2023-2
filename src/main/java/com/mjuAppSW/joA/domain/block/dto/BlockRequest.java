package com.mjuAppSW.joA.domain.block.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "사용자 차단 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class BlockRequest {
    @Schema(description = "차단을 수행하는 사용자의 세션 id")
    private final Long blockerId;
    @Schema(description = "차단을 당할 사용자의 pk")
    private final Long blockedId;
}
