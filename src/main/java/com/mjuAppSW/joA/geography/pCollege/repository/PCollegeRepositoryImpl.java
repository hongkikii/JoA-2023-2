package com.mjuAppSW.joA.geography.pCollege.repository;

import com.mjuAppSW.joA.geography.pCollege.entity.PCollege;
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
