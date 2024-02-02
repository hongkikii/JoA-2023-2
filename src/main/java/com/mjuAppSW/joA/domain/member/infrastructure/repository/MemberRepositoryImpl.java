package com.mjuAppSW.joA.domain.member.infrastructure.repository;

import com.mjuAppSW.joA.domain.college.MCollege;
import com.mjuAppSW.joA.domain.member.Member;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findById(id);
    }

    @Override
    public Optional<Member> findByloginId(String loginId) {
        return memberJpaRepository.findByloginId(loginId);
    }

    @Override
    public Optional<Member> findBysessionId(Long sessionId) {
        return memberJpaRepository.findBysessionId(sessionId);
    }

    @Override
    public Optional<Member> findByuEmailAndcollege(String uEmail, MCollege college) {
        return memberJpaRepository.findByuEmailAndcollege(uEmail, college);
    }

    @Override
    public Optional<Member> findForbidden(String uEmail, MCollege college) {
        return memberJpaRepository.findForbidden(uEmail, college);
    }


    @Override
    public List<Member> findAll() {
        return memberJpaRepository.findAll();
    }

    @Override
    public List<Member> findJoiningAll() {
        return memberJpaRepository.findJoiningAll();
    }

    @Override
    public void delete(Member member) {
        memberJpaRepository.delete(member);
    }

    @Override
    public void deleteAll() {
        memberJpaRepository.deleteAll();
    }
}
