package com.mjuAppSW.joA.geography.pCollege.repository;

import com.mjuAppSW.joA.geography.pCollege.entity.PCollege;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PCollegeJpaRepository extends JpaRepository<PCollege, Long> {
}
