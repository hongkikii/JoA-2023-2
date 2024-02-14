package com.mjuAppSW.joA.domain.voteCategory.repository;

import com.mjuAppSW.joA.domain.voteCategory.entity.VoteCategory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoteCategoryRepositoryImpl implements VoteCategoryRepository {

    private final VoteCategoryJpaRepository voteCategoryJpaRepository;

    @Override
    public Optional<VoteCategory> findById(Long id) {
        return voteCategoryJpaRepository.findById(id);
    }
}
