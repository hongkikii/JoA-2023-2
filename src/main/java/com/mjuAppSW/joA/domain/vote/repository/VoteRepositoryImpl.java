package com.mjuAppSW.joA.domain.vote.repository;

import com.mjuAppSW.joA.domain.vote.Vote;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoteRepositoryImpl implements VoteRepository {

    private final VoteJpaRepository voteJpaRepository;

    @Override
    public void save(Vote vote) {
        voteJpaRepository.save(vote);
    }

    @Override
    public Optional<Vote> findById(Long id) {
        return voteJpaRepository.findById(id);
    }

    @Override
    public List<String> findVoteCategoryById(Long id, PageRequest pageRequest) {
        return voteJpaRepository.findVoteCategoryById(id, pageRequest);
    }

    @Override
    public Optional<Vote> findTodayVote(Long giveId, Long takeId, Long categoryId) {
        return voteJpaRepository.findTodayVote(giveId, takeId, categoryId);
    }

    @Override
    public List<Vote> findInvalidVotes(Long giveId, Long takeId) {
        return voteJpaRepository.findInvalidVotes(giveId, takeId);
    }

    @Override
    public List<Vote> findValidAllByTakeId(Long id, Pageable pageable) {
        return voteJpaRepository.findValidAllByTakeId(id, pageable);
    }
}
