package com.mjuAppSW.joA.domain.member.controller;

import com.mjuAppSW.joA.common.dto.SuccessResponse;
import com.mjuAppSW.joA.domain.member.service.AccountService;
import com.mjuAppSW.joA.domain.member.dto.request.LoginRequest;
import com.mjuAppSW.joA.domain.member.dto.request.TransPasswordRequest;
import com.mjuAppSW.joA.domain.member.dto.response.SessionIdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/joa/members")
public class AccountApiController {

    private final AccountService accountService;

    @Operation(summary = "로그인", description = "로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "세션 id 반환"),
            @ApiResponse(responseCode = "404-1", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-2", description = "M015: 비밀번호가 올바르지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<SessionIdResponse>> login(@RequestBody @Valid LoginRequest request) {
        return SuccessResponse.of(accountService.login(request))
                .asHttp(HttpStatus.OK);
    }

    @Operation(summary = "로그아웃", description = "로그아웃 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/{id}/logout")
    public ResponseEntity<Void> logout(@PathVariable("id") @NotNull Long sessionId) {
        accountService.logout(sessionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "아이디 찾기", description = "아이디 찾기 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "로그인 id 웹메일 전송 후 HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-1", description = "P001: 학교 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-2", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/id/find")
    public ResponseEntity<Void> findId(@RequestParam @NotBlank String collegeEmail, @RequestParam @NotNull Long collegeId) {
        accountService.findLoginId(collegeEmail, collegeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "비밀번호 찾기", description = "비밀번호 찾기 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "임시 비밀번호 웹메일 전송 후 HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/password/find")
    public ResponseEntity<Void> findPassword(@RequestParam @NotBlank String loginId) {
        accountService.findPassword(loginId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-1", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-2", description = "M015: 비밀번호가 올바르지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "M012: 올바른 비밀번호 형식이 아닙니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PatchMapping("/password")
    public ResponseEntity<Void> transPassword(@RequestBody @Valid TransPasswordRequest request) {
        accountService.transPassword(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "M003: S3 저장소 접근에 실패했습니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdrawal(@PathVariable("id") @NotNull Long sessionId) {
        accountService.withdrawal(sessionId);
        return ResponseEntity.noContent().build();
    }
}
