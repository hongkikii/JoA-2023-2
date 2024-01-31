package com.mjuAppSW.joA.domain.room;

import java.time.LocalDateTime;

import com.mjuAppSW.joA.common.dto.SuccessResponse;
import com.mjuAppSW.joA.domain.room.dto.response.RoomResponse;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/joa/rooms")
public class RoomApiController {

    private final RoomService roomService;

    @Operation(summary = "채팅방 생성", description = "메인 페이지에서 하트가 눌렸을 때 방을 생성하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "방 생성 완료")
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<RoomResponse>> createRoom(){
        LocalDateTime createdRoomDate = LocalDateTime.now();
        return SuccessResponse.of(roomService.createRoom(createdRoomDate)).asHttp(HttpStatus.OK);
    }
    @Operation(summary = "채팅방 생성 시간 조회", description = "채팅방 페이지에서 투표를 누르기 전 유효기간을 확인하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "HTTP 상태 코드 반환"),
        @ApiResponse(responseCode = "400", description = "R002: 방이 생성된지 24시간이 지났습니다."),
        @ApiResponse(responseCode = "404", description = "R003: 방을 찾을 수 없습니다.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Void> checkCreateAtRoom(
            @Parameter(description = "방 id", in = ParameterIn.PATH) @PathVariable("id") Long roomId){
        roomService.checkCreateAtRoom(roomId);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "채팅방 상태 연장", description = "채팅방 페이지에서 투표가 완료되었을 때 유효기간을 연장하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "HTTP 상태 코드 반환"),
        @ApiResponse(responseCode = "404", description = "R003: 방을 찾을 수 없습니다."),
        @ApiResponse(responseCode = "409", description = "R004: 이미 연장된 방입니다.")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateStatusAndDate(
         @Parameter(description = "방 id", in = ParameterIn.PATH) @PathVariable("id") Long roomId){
        LocalDateTime updateRoomStatusDate = LocalDateTime.now();
        roomService.updateStatusAndDate(roomId, updateRoomStatusDate);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "채팅방 생성 전 채팅방 유무 확인", description = "채팅방 생성 전 이미 두 사용자의 채팅방이 존재하는지 확인 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "HTTP 상태 코드 반환"),
        @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "400", description = "RIM002: 이미 두 사용자의 채팅방이 존재합니다.")
    })
    @GetMapping("/existence")
    public ResponseEntity<Void> checkRoomInMember(@RequestParam("memberId1") Long memberId1, @RequestParam("memberId2") Long memberId2){
        roomService.checkRoomInMember(memberId1, memberId2);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "채팅방 생성 전 신고된 메시지 확인", description = "채팅방 생성 전 신고된 메시지가 존재하는 사용자 조회 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "HTTP 상태 코드 반환"),
        @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "409", description = "MR003: 상대방을 신고한 메시지가 존재합니다."),
        @ApiResponse(responseCode = "409", description = "MR004: 상대방에게 신고된 메시지가 존재합니다."),
    })
    @GetMapping("/report-message")
    public ResponseEntity<Void> checkMessageReport(@RequestParam("memberId1") Long memberId1, @RequestParam("memberId2") Long memberId2){
        roomService.checkMessageReport(memberId1, memberId2);
        return ResponseEntity.noContent().build();
    }
}