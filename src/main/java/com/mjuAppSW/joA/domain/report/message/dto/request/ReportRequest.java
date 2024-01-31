package com.mjuAppSW.joA.domain.report.message.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "메시지 신고 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class ReportRequest {
    @NotNull
    private final Long messageId;
    @NotNull
    private final Long categoryId;
}
