package com.mjuAppSW.joA.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    NONE("none"),
    STEP_1_STOP("step_1_stop"),
    STEP_1_COMPLETE("step_1_complete"),
    STEP_2_STOP("step_2_stop"),
    STEP_2_COMPLETE("step_2_complete"),
    STEP_3_STOP("step_3_stop");

    private final String statusStr;
}
