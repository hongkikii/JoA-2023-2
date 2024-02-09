package com.mjuAppSW.joA.domain.voteCategory.repository;

import com.mjuAppSW.joA.domain.voteCategory.VoteCategory;
import java.util.Optional;

public interface VoteCategoryRepository {

    Optional<VoteCategory> findById(Long id);
}
