package com.mjuAppSW.joA.domain.report.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CheckMessageReportRequest {
    @JsonProperty("memberId1")
    @NotNull
    private Long memberId1;
    @JsonProperty("memberId2")
    @NotNull
    private Long memberId2;
}
