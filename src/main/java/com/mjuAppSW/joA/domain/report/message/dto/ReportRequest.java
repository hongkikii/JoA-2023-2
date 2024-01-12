package com.mjuAppSW.joA.domain.report.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportRequest {
    @JsonProperty("messageId")
    @NotNull
    private long messageId;
    @JsonProperty("categoryId")
    @NotNull
    private long categoryId;
    @JsonProperty("content")
    @NotNull
    private String content;
}
