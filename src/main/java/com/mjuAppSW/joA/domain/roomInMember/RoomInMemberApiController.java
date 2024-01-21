package com.mjuAppSW.joA.domain.roomInMember;

import com.mjuAppSW.joA.common.dto.SuccessResponse;
import com.mjuAppSW.joA.domain.roomInMember.dto.request.CheckRoomInMemberRequest;
import com.mjuAppSW.joA.domain.roomInMember.dto.request.UpdateExpiredRequest;
import com.mjuAppSW.joA.domain.roomInMember.dto.request.VoteRequest;
import com.mjuAppSW.joA.domain.roomInMember.dto.response.RoomListResponse;
import com.mjuAppSW.joA.domain.roomInMember.dto.response.UserInfoResponse;
import com.mjuAppSW.joA.domain.roomInMember.dto.response.VoteResponse;

import io.swagger.v3.oas.annotations.Operation;
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
        @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "403", description = "M004: 정지된 계정입니다."),
        @ApiResponse(responseCode = "404", description = "RIM001: 채팅방을 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "MG003: 메시지 복호화에 실패했습니다."),
    })
    @GetMapping("/{memberId}")
    public ResponseEntity<SuccessResponse<RoomListResponse>> getRoomList(@PathVariable("memberId") Long memberId){
        return SuccessResponse.of(roomInMemberService.getRoomList(memberId))
            .asHttp(HttpStatus.OK);
    }

    @Operation(summary = "채팅방 연장 투표 저장 및 상대방 투표 유무 확인", description = "채팅방 연장 투표 저장 및 상대방 투표 유무 확인 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "채팅방 연장 투표 저장 및 상대방 투표 유무 반환"),
        @ApiResponse(responseCode = "404", description = "R003: 방을 찾을 수 없습니다."),
        @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "404", description = "RIM001: 채팅방을 찾을 수 없습니다.")
    })
    @PostMapping("/result")
    public ResponseEntity<SuccessResponse<VoteResponse>> saveVoteResult(@RequestBody @Valid VoteRequest request){
        return SuccessResponse.of(roomInMemberService.saveVoteResult(request))
            .asHttp(HttpStatus.OK);
    }

    @Operation(summary = "채팅방 퇴장", description = "채팅방 퇴장 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상태코드 반환"),
        @ApiResponse(responseCode = "404", description = "R003: 방을 찾을 수 없습니다."),
        @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "404", description = "RIM001: 채팅방을 찾을 수 없습니다.")
    })
    @PatchMapping("/expired")
    public ResponseEntity<Void> updateExpired(@RequestBody @Valid UpdateExpiredRequest request){
        roomInMemberService.updateExpired(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 생성 전 채팅방 유무 확인", description = "채팅 방 생성 전 이미 두 사용자의 채팅방이 존재하는지 확인 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상태코드 반환"),
        @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "400", description = "RIM002: 이미 두 사용자의 채팅방이 존재합니다.")
    })
    @PostMapping("/check")
    public ResponseEntity<Void> checkRoomInMember(@RequestBody @Valid CheckRoomInMemberRequest request){
        roomInMemberService.checkRoomInMember(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 입장시 상대방 정보 조회", description = "상대방 정보 조회 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상대방 정보 반환"),
        @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "404", description = "R003: 방을 찾을 수 없습니다."),
        @ApiResponse(responseCode = "404", description = "RIM001: 채팅방을 찾을 수 없습니다.")
    })
    @GetMapping("/{roomId}/{memberId}/userInfo")
    public ResponseEntity<SuccessResponse<UserInfoResponse>> getUserInfo(@PathVariable("roomId") Long roomId, @PathVariable("memberId") Long memberId){
        return SuccessResponse.of(roomInMemberService.getUserInfo(roomId, memberId))
            .asHttp(HttpStatus.OK);
    }
}