package com.mjuAppSW.joA.domain.college;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MCollegeRepository extends JpaRepository<MCollege, Long> {

    Optional<MCollege> findBydomain(String domain);
}
