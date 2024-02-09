package com.mjuAppSW.joA.domain.member.infrastructure.repository;

import com.mjuAppSW.joA.domain.college.MCollege;
import com.mjuAppSW.joA.domain.member.Member;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findByloginId(String loginId);

    Optional<Member> findBysessionId(Long sessionId);

    Optional<Member> findByuEmailAndcollege(String uEmail, MCollege college);

    Optional<Member> findForbidden(String uEmail, MCollege college);

    List<Member> findAll();
    
    List<Member> findJoiningAll();

}
