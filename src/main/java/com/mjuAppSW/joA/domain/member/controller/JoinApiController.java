package com.mjuAppSW.joA.domain.member.controller;

import com.mjuAppSW.joA.domain.member.dto.request.JoinRequest;
import com.mjuAppSW.joA.domain.member.dto.request.VerifyIdRequest;
import com.mjuAppSW.joA.domain.member.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/joa/members")
public class JoinApiController {

    private final JoinService joinService;

    @Operation(summary = "아이디 중복 검증", description = "회원가입 시 중복 아이디가 존재하는지 확인하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400-1", description = "M010: 올바른 아이디 형식이 아닙니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400-2", description = "M008: 이메일 인증이 완료되지 않았습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "M011: 이미 사용 중인 아이디입니다.", content = @Content(schema = @Schema(hidden = true))),

    })
    @PostMapping("/id/verify")
    public ResponseEntity<Void> verifyId(@RequestBody @Valid VerifyIdRequest request) {
        joinService.verifyLoginId(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 가입", description = "회원 가입 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400-1", description = "M012: 올바른 비밀번호 형식이 아닙니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-1", description = "M007: 세션 id가 유효하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400-2", description = "M013: 아이디 중복 확인이 완료되지 않았습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-2", description = "P001: 학교 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),

    })
    @PostMapping
    public ResponseEntity<Void> join(@RequestBody @Valid JoinRequest request) {
        joinService.join(request);
        return ResponseEntity.ok().build();
    }
}
