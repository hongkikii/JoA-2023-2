package com.mjuAppSW.joA.domain.mCollege.repository;

import com.mjuAppSW.joA.domain.mCollege.entity.MCollege;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MCollegeRepositoryImpl implements MCollegeRepository {

    private final MCollegeJpaRepository mCollegeJpaRepository;

    @Override
    public Optional<MCollege> findById(Long collegeId) {
        return mCollegeJpaRepository.findById(collegeId);
    }

    @Override
    public Optional<MCollege> findByDomain(String domain) {
        return mCollegeJpaRepository.findBydomain(domain);
    }
}
