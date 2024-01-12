package com.mjuAppSW.joA.domain.vote;

import com.mjuAppSW.joA.common.dto.SuccessResponse;
import com.mjuAppSW.joA.domain.vote.dto.request.VoteRequest;
import com.mjuAppSW.joA.domain.vote.dto.response.VoteOwnerResponse;
import com.mjuAppSW.joA.domain.vote.dto.response.VoteListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
            @ApiResponse(responseCode = "200", description = "상태 코드 반환"),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "404", description = "V002: 투표 카테고리가 존재하지 않습니다."),
            @ApiResponse(responseCode = "409", description = "V003: 이미 투표가 존재합니다."),
            @ApiResponse(responseCode = "403", description = "V003: 접근 권한이 없는 계정입니다."),
            @ApiResponse(responseCode = "403", description = "V003: 차단 조치가 이루어진 계정입니다.")
    })
    @PostMapping
    public ResponseEntity<Void> sendVote(@RequestBody @Valid VoteRequest request) {
        voteService.sendVote(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "받은 투표 목록 조회", description = "받은 투표 목록 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "받은 투표 목록 반환"),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<VoteListResponse>> getVotes(
            @Parameter(description = "세션 id", in = ParameterIn.PATH)
            @PathVariable("id") Long sessionId) {
        return SuccessResponse.of(voteService.getVotes(sessionId))
                .asHttp(HttpStatus.OK);
    }

    @Operation(summary = "투표 화면 사용자 정보 조회", description = "투표 화면 사용자 정보 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "투표 화면 사용자 정보 반환"),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다.")
    })
    @GetMapping("/{id}/owner")
    public ResponseEntity<SuccessResponse<VoteOwnerResponse>> getVoteOwner(
            @Parameter(description = "사용자 세션 id", in = ParameterIn.PATH)
            @PathVariable("id") Long sessionId) {
        return SuccessResponse.of(voteService.getVoteOwner(sessionId))
                .asHttp(HttpStatus.OK);
    }
}
