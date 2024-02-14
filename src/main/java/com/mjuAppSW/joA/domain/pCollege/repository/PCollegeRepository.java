package com.mjuAppSW.joA.domain.pCollege.repository;

import com.mjuAppSW.joA.domain.pCollege.entity.PCollege;
import java.util.Optional;

public interface PCollegeRepository {

    void save(PCollege pCollege);

    Optional<PCollege> findById(Long id);
}
