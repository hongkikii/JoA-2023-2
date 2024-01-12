package com.mjuAppSW.joA.geography.block;

import com.mjuAppSW.joA.geography.block.dto.BlockRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/joa/blocks")
public class BlockApiController {

    private final BlockService blockService;
    @Operation(summary = "사용자 차단", description = "사용자 차단 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HTTP 상태 코드 반환"),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "404", description = "L001: 사용자의 위치 정보를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "409", description = "B002: 이미 차단한 사용자입니다."),
    })
    @PostMapping
    public ResponseEntity<Void> block(@RequestBody BlockRequest blockRequest) {
        blockService.block(blockRequest);
        return ResponseEntity.ok().build();
    }
}
