package com.mjuAppSW.joA.domain.vote.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class VoteContent {
    @NonNull
    private final Long voteId;
    @NonNull
    private final Long categoryId;

    private final String hint;
}
