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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/joa/rooms")
public class RoomApiController {

    private final RoomService roomService;

    @Operation(summary = "방 생성", description = "메인 페이지에서 하트가 눌렸을 때 방을 생성하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "방 생성 완료")
    })
    @PostMapping()
    public ResponseEntity<SuccessResponse<RoomResponse>> createRoom(){
        LocalDateTime createdRoomDate = LocalDateTime.now();
        return SuccessResponse.of(roomService.createRoom(createdRoomDate)).asHttp(HttpStatus.OK);
    }
    @Operation(summary = "방 생성 시간 조회", description = "채팅방 페이지에서 투표를 누르기 전 유효기간을 확인하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "방이 생성된지 24시간이 지나지 않았습니다."),
        @ApiResponse(responseCode = "404", description = "R003: 방을 찾을 수 없습니다."),
        @ApiResponse(responseCode = "400", description = "R002: 방이 생성된지 24시간이 지났습니다.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Void> checkCreateAtRoom(
            @Parameter(description = "방 id", in = ParameterIn.PATH) @PathVariable("id") Long roomId){
        roomService.checkCreateAtRoom(roomId);
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "방 상태 연장", description = "채팅방 페이지에서 투표가 완료되었을 때 유효기간을 연장하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "방 연장 완료"),
        @ApiResponse(responseCode = "404", description = "R003: 방을 찾을 수 없습니다."),
        @ApiResponse(responseCode = "409", description = "R004: 이미 연장된 방입니다.")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateStatusAndDate(
         @Parameter(description = "방 id", in = ParameterIn.PATH) @PathVariable("id") Long roomId){
        LocalDateTime updateRoomStatusDate = LocalDateTime.now();
        roomService.updateStatusAndDate(roomId, updateRoomStatusDate);
        return ResponseEntity.ok().build();
    }
}