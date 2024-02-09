package com.mjuAppSW.joA.geography.college.repository;

import com.mjuAppSW.joA.geography.college.PCollege;
import java.util.Optional;

public interface PCollegeRepository {

    void save(PCollege pCollege);

    Optional<PCollege> findById(Long id);
}
