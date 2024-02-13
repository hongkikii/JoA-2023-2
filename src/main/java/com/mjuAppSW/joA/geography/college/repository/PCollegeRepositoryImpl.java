package com.mjuAppSW.joA.geography.college.repository;

import com.mjuAppSW.joA.geography.college.entity.PCollege;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PCollegeRepositoryImpl implements PCollegeRepository {

    private final PCollegeJpaRepository pCollegeJpaRepository;

    @Override
    public void save(PCollege pCollege) {
        pCollegeJpaRepository.save(pCollege);
    }

    @Override
    public Optional<PCollege> findById(Long id) {
        return pCollegeJpaRepository.findById(id);
    }
}
