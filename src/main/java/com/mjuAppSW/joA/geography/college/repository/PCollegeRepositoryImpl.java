package com.mjuAppSW.joA.geography.college.repository;

import com.mjuAppSW.joA.geography.college.PCollege;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PCollegeRepositoryImpl implements PCollegeRepository {

    private final PCollegeJpaRepository pCollegeJpaRepository;

    @Override
    public PCollege save(PCollege pCollege) {
        return pCollegeJpaRepository.save(pCollege);
    }

    @Override
    public Optional<PCollege> findById(Long id) {
        return pCollegeJpaRepository.findById(id);
    }
}
