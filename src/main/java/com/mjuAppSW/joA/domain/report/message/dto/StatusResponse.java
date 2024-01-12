package com.mjuAppSW.joA.domain.report.message.dto;

import lombok.Getter;

@Getter
public class StatusResponse {
    private Integer status;
    public StatusResponse(Integer status) {
        this.status = status;
    }
}
