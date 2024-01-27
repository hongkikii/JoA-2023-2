package com.mjuAppSW.joA.domain.member.infrastructure.repository;

import com.mjuAppSW.joA.domain.college.MCollegeEntity;
import com.mjuAppSW.joA.domain.member.Member;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findByloginId(String loginId);

    Optional<Member> findBysessionId(Long sessionId);

    Optional<Member> findByuEmailAndcollege(String uEmail, MCollegeEntity college);

    Optional<Member> findForbidden(String uEmail, MCollegeEntity college);

    List<Member> findAll();
    
    List<Member> findJoiningAll();
}
