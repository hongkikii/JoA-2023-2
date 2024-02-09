package com.mjuAppSW.joA.domain.college.repository;

import com.mjuAppSW.joA.domain.college.MCollege;
import java.util.Optional;

public interface MCollegeRepository {

    Optional<MCollege> findById(Long collegeId);

    Optional<MCollege> findByDomain(String domain);
}
