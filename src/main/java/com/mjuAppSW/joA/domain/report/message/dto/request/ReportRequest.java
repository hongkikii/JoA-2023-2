package com.mjuAppSW.joA.domain.report.message.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReportRequest {
    @NotNull
    private Long messageId;
    @NotNull
    private Long categoryId;
}
