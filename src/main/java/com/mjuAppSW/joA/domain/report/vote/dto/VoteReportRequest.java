package com.mjuAppSW.joA.domain.report.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "투표 신고 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class VoteReportRequest {
    @NotNull
    private final Long voteId;
    @NotNull
    private final Long reportId;
    private final String content;
}
