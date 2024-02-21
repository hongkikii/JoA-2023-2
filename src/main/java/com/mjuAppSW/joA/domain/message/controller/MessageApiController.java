package com.mjuAppSW.joA.domain.message.controller;

import com.mjuAppSW.joA.domain.message.dto.response.MessageResponse;
import com.mjuAppSW.joA.domain.message.service.MessageService;
import com.mjuAppSW.joA.common.dto.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/joa/messages")
public class MessageApiController {
    private final MessageService messageService;

    @Operation(summary = "메시지 조회", description = "채팅방 페이지에서 화면이 로드 될 때 메시지를 조회하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메시지 조회 완료"),
        @ApiResponse(responseCode = "404-1", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404-2", description = "R003: 채팅방을 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404-3", description = "RIM001: 사용자와 연결된 채팅방을 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "500", description = "MG003: 메시지 복호화를 실패했습니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<MessageResponse>> get(
        @RequestParam("roomId") Long roomId, @RequestParam("memberId") Long memberId){
            return SuccessResponse.of(messageService.get(roomId, memberId))
                .asHttp(HttpStatus.OK);
    }
}