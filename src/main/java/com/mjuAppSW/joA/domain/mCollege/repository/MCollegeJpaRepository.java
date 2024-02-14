package com.mjuAppSW.joA.domain.mCollege.repository;

import com.mjuAppSW.joA.domain.mCollege.entity.MCollege;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MCollegeJpaRepository extends JpaRepository<MCollege, Long> {

    Optional<MCollege> findBydomain(String domain);
}
