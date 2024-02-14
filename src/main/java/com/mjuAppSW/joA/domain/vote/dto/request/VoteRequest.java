package com.mjuAppSW.joA.domain.vote.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "투표 전송 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class VoteRequest {
    @Schema(description = "투표를 보내는 사용자의 세션 id")
    @NotNull
    private final Long giveId;
    @Schema(description = "투표를 받는 사용자의 pk")
    @NotNull
    private final Long takeId;
    @NotNull
    private final Long categoryId;
    @Size(max = 15)
    private final String hint;
}
