package com.mjuAppSW.joA.domain.vote;

import com.mjuAppSW.joA.domain.vote.exception.InvalidVoteExistedException;
import com.mjuAppSW.joA.domain.vote.exception.VoteAlreadyExistedException;
import com.mjuAppSW.joA.domain.vote.repository.VoteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteQueryService {

    private final VoteRepository voteRepository;

    public void validateNoTodayVote(Long giveId, Long takeId, Long categoryId) {
        voteRepository.findTodayBy(giveId, takeId, categoryId)
                .ifPresent(vote -> {
                    throw new VoteAlreadyExistedException();});
    }

    public void validateNoInvalidVotes(Long giveId, Long takeId) {
        if (!voteRepository.findInvalidAllBy(giveId, takeId).isEmpty()) {
            throw new InvalidVoteExistedException();
        }
    }

    public List<Vote> getValidAllBy(Long takeId) {
        Pageable pageable = PageRequest.of(0, 30);
        return  voteRepository.findValidAllBy(takeId, pageable);
    }
}
