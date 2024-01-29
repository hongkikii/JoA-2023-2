package com.mjuAppSW.joA.domain.room.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "채팅방 생성 전 신고된 메시지 확인 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class CheckMessageReportRequest {
    @NotNull
    private final Long memberId1;
    @NotNull
    private final Long memberId2;
}
