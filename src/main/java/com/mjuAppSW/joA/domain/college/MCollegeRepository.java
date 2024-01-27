package com.mjuAppSW.joA.domain.college;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MCollegeRepository extends JpaRepository<MCollegeEntity, Long> {

    Optional<MCollegeEntity> findBydomain(String domain);
}
