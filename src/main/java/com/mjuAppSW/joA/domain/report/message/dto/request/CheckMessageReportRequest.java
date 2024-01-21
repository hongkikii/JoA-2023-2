package com.mjuAppSW.joA.domain.report.message.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "채팅방 생성 전 신고된 메시지 확인 Request")
public class CheckMessageReportRequest {
    @NotNull
    private Long memberId1;
    @NotNull
    private Long memberId2;
}
