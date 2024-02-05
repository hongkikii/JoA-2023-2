package com.mjuAppSW.joA.common.exception;

import com.mjuAppSW.joA.common.dto.ErrorResponse;
import com.mjuAppSW.joA.slack.SlackService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final SlackService slackService;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(HttpServletRequest request, BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        if (errorCode.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            log.error("handleBusinessException", e);
        } else {
            log.warn("handleBusinessException", e);
        }
        slackService.sendSlackMessageProductError(request, e);
        return makeErrorResponse(errorCode);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(HttpServletRequest request, Exception e) {
        log.error("handleException", e);
        slackService.sendSlackMessageProductError(request, e);
        return makeErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> makeErrorResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }
}
