package com.mjuAppSW.joA.domain.college.repository;

import com.mjuAppSW.joA.domain.college.MCollege;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MCollegeJpaRepository extends JpaRepository<MCollege, Long> {

    Optional<MCollege> findBydomain(String domain);
}
