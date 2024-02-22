package com.mjuAppSW.joA.domain.roomInMember.controller;

import com.mjuAppSW.joA.domain.member.dto.response.ChattingPageResponse;
import com.mjuAppSW.joA.domain.roomInMember.service.RoomInMemberService;
import com.mjuAppSW.joA.common.dto.SuccessResponse;
import com.mjuAppSW.joA.domain.roomInMember.dto.request.UpdateExpiredRequest;
import com.mjuAppSW.joA.domain.roomInMember.dto.request.VoteRequest;
import com.mjuAppSW.joA.domain.roomInMember.dto.response.RoomListResponse;
import com.mjuAppSW.joA.domain.roomInMember.dto.response.VoteResponse;

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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/joa/room-in-members")
public class RoomInMemberApiController {
    private final RoomInMemberService roomInMemberService;
    @Operation(summary = "채팅방 목록 페이지 정보 조회", description = "채팅방 목록 페이지에서 채팅 목록 조회 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "채팅방 목록 정보 반환"),
        @ApiResponse(responseCode = "404-1", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403-1", description = "M004: 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "403-2", description = "M014: 영구 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404-2", description = "RIM001: 사용자와 연결된 채팅방을 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "500", description = "MG003: 메시지 복호화에 실패했습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/{memberId}")
    public ResponseEntity<SuccessResponse<RoomListResponse>> getChattingRoomListPage(@PathVariable("memberId") Long memberId){
        return SuccessResponse.of(roomInMemberService.getChattingRoomListPage(memberId))
            .asHttp(HttpStatus.OK);
    }

    @Operation(summary = "채팅방 연장 투표 저장 및 상대방 투표 유무 확인", description = "채팅방 연장 투표 저장 및 상대방 투표 유무 확인 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "채팅방 연장 투표 저장 및 상대방 투표 유무 반환"),
        @ApiResponse(responseCode = "404-1", description = "R003: 채팅방을 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404-2", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404-3", description = "RIM001: 채팅방을 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "409", description = "RIM003: 이미 채팅방 연장에 대한 투표가 존재합니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping("/result")
    public ResponseEntity<SuccessResponse<VoteResponse>> saveVote(@RequestBody @Valid VoteRequest request){
        return SuccessResponse.of(roomInMemberService.saveVote(request))
            .asHttp(HttpStatus.OK);
    }

    @Operation(summary = "채팅방 퇴장", description = "채팅방 퇴장 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "HTTP 상태 코드 반환"),
        @ApiResponse(responseCode = "404-1", description = "R003: 채팅방을 찾을 수 없습니다."),
        @ApiResponse(responseCode = "404-2", description = "M001: 사용자를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "404-3", description = "RIM001: 사용자와 연결된 채팅방을 찾을 수 없습니다.")
    })
    @PatchMapping("/out")
    public ResponseEntity<Void> updateExpired(@RequestBody @Valid UpdateExpiredRequest request){
        roomInMemberService.updateExpired(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "채팅방 입장시 상대방 정보 조회", description = "상대방 정보 조회 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상대방 정보 반환"),
        @ApiResponse(responseCode = "404-1", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404-2", description = "R003: 채팅방을 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "404-3", description = "RIM001: 사용자와 연결된 채팅방을 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/chatting-page")
    public ResponseEntity<SuccessResponse<ChattingPageResponse>> getChattingPage(@RequestParam("roomId") Long roomId, @RequestParam("memberId") Long memberId){
        return SuccessResponse.of(roomInMemberService.getChattingPage(roomId, memberId))
            .asHttp(HttpStatus.OK);
    }
}