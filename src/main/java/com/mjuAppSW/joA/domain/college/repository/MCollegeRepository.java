package com.mjuAppSW.joA.domain.college.repository;

import com.mjuAppSW.joA.domain.college.MCollege;
import java.util.Optional;

public interface MCollegeRepository {

    //FIXME : 오직 테스트 때문에 도입
    void save(MCollege mCollege);

    Optional<MCollege> findById(Long collegeId);

    Optional<MCollege> findByDomain(String domain);
}
