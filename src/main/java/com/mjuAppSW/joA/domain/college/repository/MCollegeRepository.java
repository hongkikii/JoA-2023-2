package com.mjuAppSW.joA.domain.college.repository;

import com.mjuAppSW.joA.domain.college.MCollege;
import java.util.Optional;

public interface MCollegeRepository {
    void save(MCollege mCollege);
    Optional<MCollege> findById(Long collegeId);
    Optional<MCollege> findByDomain(String domain);
}
