package com.mjuAppSW.joA.domain.vote.voteCategory;

import com.mjuAppSW.joA.domain.vote.exception.VoteCategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteCategoryQueryService {

    private final VoteCategoryRepository voteCategoryRepository;

    public VoteCategory getBy(Long id) {
        return voteCategoryRepository.findById(id)
                .orElseThrow(VoteCategoryNotFoundException::new);
    }
}
