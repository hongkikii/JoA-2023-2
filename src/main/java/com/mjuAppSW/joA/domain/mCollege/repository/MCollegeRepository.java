package com.mjuAppSW.joA.domain.mCollege.repository;

import com.mjuAppSW.joA.domain.mCollege.entity.MCollege;
import java.util.Optional;

public interface MCollegeRepository {

    Optional<MCollege> findById(Long collegeId);

    Optional<MCollege> findByDomain(String domain);
}
