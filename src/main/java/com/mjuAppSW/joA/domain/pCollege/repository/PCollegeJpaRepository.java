package com.mjuAppSW.joA.domain.pCollege.repository;

import com.mjuAppSW.joA.domain.pCollege.entity.PCollege;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;

@Qualifier("secondaryDataSource")
public interface PCollegeJpaRepository extends JpaRepository<PCollege, Long> {
}
