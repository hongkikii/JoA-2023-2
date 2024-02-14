package com.mjuAppSW.joA.geography.pCollege.repository;

import com.mjuAppSW.joA.geography.pCollege.entity.PCollege;
import java.util.Optional;

public interface PCollegeRepository {

    void save(PCollege pCollege);

    Optional<PCollege> findById(Long id);
}
