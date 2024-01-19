package com.mjuAppSW.joA.domain.member;

import com.mjuAppSW.joA.common.dto.SuccessResponse;
import com.mjuAppSW.joA.domain.member.dto.request.FindIdRequest;
import com.mjuAppSW.joA.domain.member.dto.request.FindPasswordRequest;
import com.mjuAppSW.joA.domain.member.dto.request.JoinRequest;
import com.mjuAppSW.joA.domain.member.dto.request.LoginRequest;
import com.mjuAppSW.joA.domain.member.dto.request.TransPasswordRequest;
import com.mjuAppSW.joA.domain.member.dto.request.SendCertifyNumRequest;
import com.mjuAppSW.joA.domain.member.dto.request.VerifyIdRequest;
import com.mjuAppSW.joA.domain.member.dto.response.SessionIdResponse;
import com.mjuAppSW.joA.domain.member.dto.request.VerifyCertifyNumRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
public class MemberApiController {

    private final MemberService memberService;

    @Operation(summary = "인증 번호 전송", description = "회원가입 시 학교 웹메일을 확인하기 위해 해당 웹메일로 인증번호를 전송하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 번호 웹메일 전송 후 확인 코드 반환"),
            @ApiResponse(responseCode = "404", description = "P001: 학교 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409-1", description = "M005: 이미 존재하는 사용자입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "M014: 영구 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409-2", description = "M006: 사용 중인 이메일입니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/certify-num/send")
    public ResponseEntity<SuccessResponse<SessionIdResponse>> sendCertifyNum(@RequestBody @Valid SendCertifyNumRequest request) {
        return SuccessResponse.of(memberService.sendCertifyNum(request))
                .asHttp(HttpStatus.OK);
    }

    @Operation(summary = "인증 번호 검증", description = "회원가입 시 학교 웹메일을 확인하기 위해 전송된 인증번호를 확인하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "M007: 세션 id가 유효하지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "M009: 인증번호가 올바르지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/certify-num/verify")
    public ResponseEntity<Void> verifyCertifyNum(@RequestBody @Valid VerifyCertifyNumRequest request) {
        memberService.verifyCertifyNum(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "아이디 중복 검증", description = "회원가입 시 중복 아이디가 존재하는지 확인하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400-1", description = "M010: 올바른 아이디 형식이 아닙니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400-2", description = "M008: 이메일 인증이 완료되지 않았습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "M011: 이미 사용 중인 아이디입니다.", content = @Content(schema = @Schema(hidden = true))),

    })
    @PostMapping("/id/verify")
    public ResponseEntity<Void> verifyId(@RequestBody @Valid VerifyIdRequest request) {
        memberService.verifyId(request);
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
        memberService.join(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인", description = "로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "세션 id 반환"),
            @ApiResponse(responseCode = "404-1", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-2", description = "M015: 비밀번호가 올바르지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<SessionIdResponse>> login(@RequestBody @Valid LoginRequest request) {
        return SuccessResponse.of(memberService.login(request))
                .asHttp(HttpStatus.OK);
    }

    @Operation(summary = "로그아웃", description = "로그아웃 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/{id}/logout")
    public ResponseEntity<Void> logout(@PathVariable("id") @NotNull Long sessionId) {
        memberService.logout(sessionId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "아이디 찾기", description = "아이디 찾기 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 id 웹메일 전송 후 HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-1", description = "P001: 학교 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-2", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/id/find")
    public ResponseEntity<Void> findId(@RequestBody @Valid FindIdRequest request) {
        memberService.findId(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 찾기", description = "비밀번호 찾기 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "임시 비밀번호 웹메일 전송 후 HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/password/find")
    public ResponseEntity<Void> findPassword(@RequestBody @Valid FindPasswordRequest request) {
        memberService.findPassword(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-1", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-2", description = "M015: 비밀번호가 올바르지 않습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "M012: 올바른 비밀번호 형식이 아닙니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PatchMapping("/password")
    public ResponseEntity<Void> transPassword(@RequestBody @Valid TransPasswordRequest request) {
        memberService.transPassword(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "M003: S3 저장소 접근에 실패했습니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdrawal(@PathVariable("id") @NotNull Long sessionId) {
        memberService.withdrawal(sessionId);
        return ResponseEntity.ok().build();
    }
}
