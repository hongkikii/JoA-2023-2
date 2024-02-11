package com.mjuAppSW.joA.domain.member.controller;

import com.mjuAppSW.joA.common.dto.SuccessResponse;
import com.mjuAppSW.joA.domain.member.dto.request.SendCertifyNumRequest;
import com.mjuAppSW.joA.domain.member.dto.request.VerifyCertifyNumRequest;
import com.mjuAppSW.joA.domain.member.dto.response.SessionIdResponse;
import com.mjuAppSW.joA.domain.member.service.CertifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/joa/members")
public class CertifyApiController {

    private final CertifyService certifyService;

    @Operation(summary = "인증 번호 전송", description = "회원가입 시 학교 웹메일을 확인하기 위해 해당 웹메일로 인증번호를 전송하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 번호 웹메일 전송 후 확인 코드 반환"),
            @ApiResponse(responseCode = "404", description = "P001: 학교 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409-1", description = "M005: 이미 존재하는 사용자입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "M014: 영구 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409-2", description = "M006: 회원가입 중인 이메일입니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/certify-num/send")
    public ResponseEntity<SuccessResponse<SessionIdResponse>> sendCertifyNum(@RequestBody @Valid SendCertifyNumRequest request) {
        return SuccessResponse.of(certifyService.send(request))
                .asHttp(HttpStatus.OK);
    }

    @Operation(summary = "인증 번호 검증", description = "회원가입 시 학교 웹메일을 확인하기 위해 전송된 인증번호를 확인하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "M007: 세션 id가 유효하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "M009: 인증번호가 올바르지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/certify-num/verify")
    public ResponseEntity<Void> verifyCertifyNum(@RequestBody @Valid VerifyCertifyNumRequest request) {
        certifyService.verify(request);
        return ResponseEntity.noContent().build();
    }
}
