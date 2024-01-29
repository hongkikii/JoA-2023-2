package com.mjuAppSW.joA.domain.report.message;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mjuAppSW.joA.domain.report.message.dto.request.ReportRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/joa/reports")
@Slf4j
public class MessageReportApiController {

    private final MessageReportService messageReportService;
    @Operation(summary = "메시지 신고", description = "메시지 신고 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상태 코드 응답"),
        @ApiResponse(responseCode = "404", description = "RC001: 신고 카테고리가 존재하지 않습니다."),
        @ApiResponse(responseCode = "404", description = "MG001: 메시지를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "409", description = "MR001: 이미 신고된 메시지가 존재합니다."),
    })
    @PostMapping("/message")
    public ResponseEntity<Void> messageReport(@RequestBody @Valid ReportRequest request){
        LocalDateTime messageReportDate = LocalDateTime.now();
        messageReportService.messageReport(request, messageReportDate);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "신고 메시지 삭제, 프론트에 적용되는 코드 X", description = "신고 메시지 삭제 API, 프론트에 적용되는 코드X")
    @DeleteMapping("/{messageReportId}/message")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상태 코드 응답"),
        @ApiResponse(responseCode = "404", description = "MR002: 신고된 메시지를 찾을 수 없습니다."),
    })
    public ResponseEntity<Void> deleteMessageReportAdmin(@PathVariable("messageReportId") Long messageReportId){
        messageReportService.deleteMessageReportAdmin(messageReportId);
        return ResponseEntity.ok().build();
    }
}
