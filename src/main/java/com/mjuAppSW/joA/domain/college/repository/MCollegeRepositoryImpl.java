package com.mjuAppSW.joA.domain.college.repository;

import com.mjuAppSW.joA.domain.college.MCollege;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MCollegeRepositoryImpl implements MCollegeRepository {

    private final MCollegeJpaRepository mCollegeJpaRepository;

    @Override
    public void save(MCollege mCollege) {
        mCollegeJpaRepository.save(mCollege);
    }

    @Override
    public Optional<MCollege> findById(Long collegeId) {
        return mCollegeJpaRepository.findById(collegeId);
    }

    @Override
    public Optional<MCollege> findByDomain(String domain) {
        return mCollegeJpaRepository.findBydomain(domain);
    }
}
