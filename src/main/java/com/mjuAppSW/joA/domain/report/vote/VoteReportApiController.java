package com.mjuAppSW.joA.domain.report.vote;

import com.mjuAppSW.joA.domain.report.vote.dto.VoteReportRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/joa/reports")
public class VoteReportApiController {

    private final VoteReportService voteReportService;

    @Operation(summary = "투표 신고", description = "투표 신고 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HTTP 상태 코드 반환"),
            @ApiResponse(responseCode = "404", description = "RC001: 신고 카테고리가 존재하지 않습니다."),
            @ApiResponse(responseCode = "404", description = "V001: 투표가 존재하지 않습니다."),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "409", description = "VR001: 이미 투표 신고가 존재합니다."),
    })
    @PostMapping("/vote")
    public ResponseEntity<Void> reportVote(@RequestBody @Valid VoteReportRequest request) {
        voteReportService.reportVote(request);
        return ResponseEntity.ok().build();
    }
}
