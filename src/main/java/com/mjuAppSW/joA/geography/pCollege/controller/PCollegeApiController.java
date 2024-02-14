package com.mjuAppSW.joA.geography.pCollege.controller;

import com.mjuAppSW.joA.geography.pCollege.dto.PolygonRequest;
import com.mjuAppSW.joA.geography.pCollege.service.PCollegeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
@RequestMapping("/joa/colleges")
public class PCollegeApiController {

    private final PCollegeService pCollegeService;

    @Operation(summary = "학교 범위 생성", description = "학교 범위 생성 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "HTTP 상태 코드 반환", content = @Content(schema = @Schema(hidden = true))),
    })
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid PolygonRequest request) {
        pCollegeService.create(request);
        return ResponseEntity.noContent().build();
    }
}
