package com.mjuAppSW.joA.domain.voteCategory.service;

import static com.mjuAppSW.joA.common.exception.BusinessException.*;

import com.mjuAppSW.joA.domain.voteCategory.entity.VoteCategory;
import com.mjuAppSW.joA.domain.voteCategory.repository.VoteCategoryRepository;
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
                .orElseThrow(() -> VoteCategoryNotFoundException);
    }
}
