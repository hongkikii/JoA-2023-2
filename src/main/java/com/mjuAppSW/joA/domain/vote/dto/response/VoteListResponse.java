package com.mjuAppSW.joA.domain.vote.dto.response;

import com.mjuAppSW.joA.domain.vote.entity.Vote;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
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

    public static VoteListResponse of(List<Vote> votes) {
        List<VoteContent> voteList = votes.stream()
                .map(vote -> createVoteContent(vote))
                .collect(Collectors.toList());

        return VoteListResponse.builder()
                .voteList(voteList)
                .build();
    }

    private static VoteContent createVoteContent(Vote vote) {
        return VoteContent.builder()
                .voteId(vote.getId())
                .categoryId(vote.getVoteCategory().getId())
                .hint(vote.getHint())
                .build();
    }
}
