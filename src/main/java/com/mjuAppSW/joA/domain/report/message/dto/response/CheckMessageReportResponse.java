package com.mjuAppSW.joA.domain.report.message.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CheckMessageReportResponse {
    private Integer status;

    @Builder
    public CheckMessageReportResponse(Integer status) {
        this.status = status;
    }

    public static CheckMessageReportResponse of(int status) {
        return CheckMessageReportResponse.builder()
            .status(status)
            .build();
    }
}
