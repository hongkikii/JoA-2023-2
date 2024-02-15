package com.mjuAppSW.joA.domain.member.infrastructure.repository;

import com.mjuAppSW.joA.domain.mCollege.entity.MCollege;
import com.mjuAppSW.joA.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Qualifier("primaryDataSource")
public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.id = :id AND m.isWithdrawal = false")
    Optional<Member> findById(@Param("id") Long id);

    @Query("SELECT m FROM Member m WHERE m.loginId = :loginId AND m.isWithdrawal = false")
    Optional<Member> findByloginId(@Param("loginId") String loginId);

    @Query("SELECT m FROM Member m WHERE m.sessionId = :sessionId AND m.isWithdrawal = false")
    Optional<Member> findBysessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT m FROM Member m WHERE m.uEmail = :uEmail AND m.college = :college AND m.isWithdrawal = false")
    Optional<Member> findByuEmailAndcollege(@Param("uEmail") String uEmail,
                                            @Param("college") MCollege college);

    @Query("SELECT m FROM Member m WHERE m.uEmail = :uEmail AND m.college = :college AND m.status = 'step_3_stop'")
    Optional<Member> findForbidden(@Param("uEmail") String uEmail,
                                   @Param("college") MCollege college);

    @Query("SELECT m FROM Member m WHERE m.isWithdrawal = false")
    List<Member> findJoiningAll();
}
