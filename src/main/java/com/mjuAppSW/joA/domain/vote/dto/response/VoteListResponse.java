package com.mjuAppSW.joA.domain.vote.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "받은 투표 목록 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VoteListResponse {
    private final List<VoteContent> voteList;

    public static VoteListResponse of (List<VoteContent> voteList) {
        return VoteListResponse.builder()
                .voteList(voteList)
                .build();
    }
}
