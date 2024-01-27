package com.mjuAppSW.joA.domain.member.infrastructure.repository;

import com.mjuAppSW.joA.domain.college.MCollegeEntity;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.MemberEntity;
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
        return memberJpaRepository.save(
                MemberEntity.fromModel(member))
                .toModel();
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findById(id)
                .map(MemberEntity::toModel);
    }

    @Override
    public Optional<Member> findByloginId(String loginId) {
        return memberJpaRepository.findByloginId(loginId)
                .map(MemberEntity::toModel);
    }

    @Override
    public Optional<Member> findBysessionId(Long sessionId) {
        return memberJpaRepository.findBysessionId(sessionId)
                .map(MemberEntity::toModel);
    }

    @Override
    public Optional<Member> findByuEmailAndcollege(String uEmail, MCollegeEntity college) {
        return memberJpaRepository.findByuEmailAndcollege(uEmail, college)
                .map(MemberEntity::toModel);
    }

    @Override
    public Optional<Member> findForbidden(String uEmail, MCollegeEntity college) {
        return memberJpaRepository.findForbidden(uEmail, college)
                .map(MemberEntity::toModel);
    }

    @Override
    public List<Member> findAll() {
        return memberJpaRepository.findAll()
                .stream().map(MemberEntity::toModel)
                .toList();
    }

    @Override
    public List<Member> findJoiningAll() {
        return memberJpaRepository.findJoiningAll()
                .stream().map(MemberEntity::toModel)
                .toList();
    }
}
