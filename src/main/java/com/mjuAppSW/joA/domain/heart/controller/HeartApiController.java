package com.mjuAppSW.joA.domain.heart.controller;

import com.mjuAppSW.joA.common.dto.SuccessResponse;
import com.mjuAppSW.joA.domain.heart.dto.HeartRequest;
import com.mjuAppSW.joA.domain.heart.dto.HeartResponse;
import com.mjuAppSW.joA.domain.heart.service.HeartService;
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
@RequestMapping("/joa/hearts")
public class HeartApiController {

    private final HeartService heartService;

    @Operation(summary = "하트 전송", description = "하트 전송 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "하트 송신자, 수신자 정보 및 매칭 여부 반환"),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403-1", description = "M003: 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403-2", description = "M014: 영구 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403-3", description = "B001: 차단 조치가 이루어진 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409-1", description = "H001: 이미 하트가 존재합니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409-2", description = "R001: 이미 채팅방이 존재합니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<HeartResponse>> sendHeart(@RequestBody @Valid HeartRequest request) {
        return SuccessResponse.of(heartService.send(request))
                .asHttp(HttpStatus.OK);
    }
}
