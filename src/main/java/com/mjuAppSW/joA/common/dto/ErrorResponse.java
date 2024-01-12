package com.mjuAppSW.joA.common.dto;

import com.mjuAppSW.joA.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "실패 Response")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    @Schema(description = "성공 여부. 항상 false 이다.", defaultValue = "false")
    private final boolean status = false;
    private final String code;

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode());
    }
}
