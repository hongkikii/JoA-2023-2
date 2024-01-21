package com.mjuAppSW.joA.domain.message;

import com.mjuAppSW.joA.common.dto.SuccessResponse;
import com.mjuAppSW.joA.domain.message.dto.response.MessageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
        @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "404", description = "R003: 방을 찾을 수 없습니다."),
        @ApiResponse(responseCode = "404", description = "RIM001: 채팅방을 찾을 수 없습니다.")
    })
    @GetMapping("/{roomId}/{memberId}/messages")
    public ResponseEntity<SuccessResponse<MessageResponse>> loadMessages(
        @Parameter(description = "방 id", in = ParameterIn.PATH) @PathVariable("roomId") Long roomId,
        @Parameter(description = "사용자 id", in = ParameterIn.PATH) @PathVariable("memberId") Long memberId){
            return SuccessResponse.of(messageService.loadMessage(roomId, memberId))
                .asHttp(HttpStatus.OK);
    }
}