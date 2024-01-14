package com.mjuAppSW.joA.domain.report.message.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CheckMessageReportRequest {
    @NotNull
    private Long memberId1;
    @NotNull
    private Long memberId2;
}
