package com.mjuAppSW.joA.geography.college.infrastructure;

import com.mjuAppSW.joA.geography.college.PCollege;
import java.util.Optional;

public interface PCollegeRepository {

    PCollege save(PCollege pCollege);

    Optional<PCollege> findById(Long id);
}
