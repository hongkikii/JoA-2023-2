package com.mjuAppSW.joA.domain.voteCategory.repository;

import com.mjuAppSW.joA.domain.voteCategory.entity.VoteCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteCategoryJpaRepository extends JpaRepository<VoteCategory, Long> {
}
