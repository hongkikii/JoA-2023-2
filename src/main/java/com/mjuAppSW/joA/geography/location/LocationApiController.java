package com.mjuAppSW.joA.geography.location;

import com.mjuAppSW.joA.common.dto.SuccessResponse;
import com.mjuAppSW.joA.geography.location.dto.response.NearByListResponse;
import com.mjuAppSW.joA.geography.location.dto.request.UpdateRequest;
import com.mjuAppSW.joA.geography.location.dto.response.UpdateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/joa/locations")
public class LocationApiController {

    private final LocationService locationService;

    @Operation(summary = "사용자 위치 업데이트", description = "사용자 위치 업데이트 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "학교 내 위치 여부 정보 반환"),
            @ApiResponse(responseCode = "404", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "M003: 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "M014: 영구 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "L001: 사용자의 위치 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "P001: 학교 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
    })
    @PatchMapping
    public ResponseEntity<SuccessResponse<UpdateResponse>> update(@RequestBody @Valid UpdateRequest request) {
        return SuccessResponse.of(locationService.update(request))
                .asHttp(HttpStatus.OK);
    }

    @Operation(summary = "주변 사람 목록 조회", description = "주변 사람 목록 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주변 사람 목록 반환"),
            @ApiResponse(responseCode = "404-1", description = "M001: 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403-1", description = "M003: 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403-2", description = "M014: 영구 정지된 계정입니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404-2", description = "L001: 사용자의 위치 정보를 찾을 수 없습니다.", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "P002: 사용자가 학교 밖에 위치합니다.", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<NearByListResponse>> getNearByList(
            @Parameter(description = "사용자 세션 id", in = ParameterIn.PATH) @PathVariable("id") @NotNull Long sessionId,
            @Parameter(description = "사용자 현재 위도", in = ParameterIn.QUERY) @RequestParam @NotBlank Double latitude,
            @Parameter(description = "사용자 현재 경도", in = ParameterIn.QUERY) @RequestParam @NotBlank Double longitude,
            @Parameter(description = "사용자 현재 고도", in = ParameterIn.QUERY) @RequestParam @NotBlank Double altitude) {
        return SuccessResponse.of(locationService.getNearByList(sessionId, latitude, longitude, altitude))
                .asHttp(HttpStatus.OK);
    }
}
