package com.mjuAppSW.joA.domain.member.infrastructure.repository;

import com.mjuAppSW.joA.domain.college.MCollegeEntity;
import com.mjuAppSW.joA.domain.member.MemberEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {

    @Query("SELECT m FROM Member m WHERE m.id = :id AND m.isWithdrawal = false")
    Optional<MemberEntity> findById(@Param("id") Long id);

    @Query("SELECT m FROM Member m WHERE m.loginId = :loginId AND m.isWithdrawal = false")
    Optional<MemberEntity> findByloginId(@Param("loginId") String loginId);

    @Query("SELECT m FROM Member m WHERE m.sessionId = :sessionId AND m.isWithdrawal = false")
    Optional<MemberEntity> findBysessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT m FROM Member m WHERE m.uEmail = :uEmail AND m.college = :college AND m.isWithdrawal = false")
    Optional<MemberEntity> findByuEmailAndcollege(@Param("uEmail") String uEmail,
                                                  @Param("college") MCollegeEntity college);

    @Query("SELECT m FROM Member m WHERE m.uEmail = :uEmail AND m.college = :college AND m.status = 3")
    Optional<MemberEntity> findForbidden(@Param("uEmail") String uEmail,
                                         @Param("college") MCollegeEntity college);

    @Query("SELECT m FROM Member m WHERE m.isWithdrawal = false")
    List<MemberEntity> findJoiningAll();
}
