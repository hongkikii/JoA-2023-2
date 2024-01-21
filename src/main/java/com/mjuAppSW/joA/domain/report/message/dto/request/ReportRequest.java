package com.mjuAppSW.joA.domain.report.message.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "메시지 신고 Request")
public class ReportRequest {
    @NotNull
    private Long messageId;
    @NotNull
    private Long categoryId;
}
