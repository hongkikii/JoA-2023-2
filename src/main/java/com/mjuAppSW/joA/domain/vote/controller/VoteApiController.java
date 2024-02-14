package com.mjuAppSW.joA.domain.vote.controller;

import com.mjuAppSW.joA.common.dto.SuccessResponse;
import com.mjuAppSW.joA.domain.vote.dto.request.VoteRequest;
import com.mjuAppSW.joA.domain.vote.dto.response.VoteListResponse;
import com.mjuAppSW.joA.domain.vote.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/joa/votes")
public class VoteApiController {
    private final VoteService voteService;

    @Operation(summary = "투표 전송", description = "투표 전송 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "상태 코드 반환"),
            @ApiResponse(responseCode = "404-1", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403-1", description = "M004: 일시 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403-2", description = "M014: 영구 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-2", description = "V002: 투표 카테고리가 존재하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "V003: 이미 투표가 존재합니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403-3", description = "V004: 투표 신고로 인해 접근이 제한된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403-4", description = "B001: 차단 조치에 의해 접근 권한이 없습니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping
    public ResponseEntity<Void> send(@RequestBody @Valid VoteRequest request) {
        voteService.send(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "받은 투표 목록 조회", description = "받은 투표 목록 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "받은 투표 목록 반환"),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403-1", description = "M004: 일시 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403-2", description = "M014: 영구 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<VoteListResponse>> get(
            @Parameter(description = "세션 id", in = ParameterIn.PATH)
            @PathVariable("id") Long sessionId) {
        return SuccessResponse.of(voteService.get(sessionId))
                .asHttp(HttpStatus.OK);
    }
}
