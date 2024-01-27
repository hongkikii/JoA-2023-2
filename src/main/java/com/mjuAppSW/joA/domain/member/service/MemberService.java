package com.mjuAppSW.joA.domain.member.service;

import com.mjuAppSW.joA.domain.college.MCollegeEntity;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.exception.MemberAlreadyExistedException;
import com.mjuAppSW.joA.domain.member.exception.PermanentBanException;
import com.mjuAppSW.joA.domain.member.infrastructure.repository.MemberRepository;
import com.mjuAppSW.joA.domain.member.exception.MemberNotFoundException;
import com.mjuAppSW.joA.geography.location.exception.AccessStoppedException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findBySessionId(Long sessionId) {
        return memberRepository.findBysessionId(sessionId)
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member findNormalBySessionId(Long sessionId) {
        return memberRepository.findBysessionId(sessionId)
                .filter(member -> {
                    if (member.getStatus() == 1 || member.getStatus() == 2) {
                        throw new AccessStoppedException();
                    }
                    if (member.getStatus() == 3) {
                        throw new PermanentBanException();
                    }
                    return true;
                })
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member findByLoginId(String loginId) {
        return memberRepository.findByloginId(loginId)
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member findByUEmailAndCollege(String uEmail, MCollegeEntity mCollegeEntity) {
        return memberRepository.findByuEmailAndcollege(uEmail, mCollegeEntity)
                .orElseThrow(MemberNotFoundException::new);
    }

    public void checkExist(String uEmail, MCollegeEntity college) {
        memberRepository.findByuEmailAndcollege(uEmail, college)
                .ifPresent(member -> {
                    throw new MemberAlreadyExistedException();});
    }

    public void checkForbidden(String uEmail, MCollegeEntity mCollegeEntity) {
        memberRepository.findForbidden(uEmail, mCollegeEntity)
                .ifPresent(forbiddenMember -> {
                    throw new PermanentBanException();});
    }

    public void checkStopped(Member member) {
        if (member.getStatus() == 1 || member.getStatus() == 2 || member.getStatus() == 3) {
            throw new AccessStoppedException();
        }
    }

    @Transactional
    public void updateUrlCode(Member member, String urlCode) {
        memberRepository.save(
                Member.updateUrlCode(member, urlCode));
    }

    @Transactional
    public void updateBio(Member member, String bio) {
        memberRepository.save(
                Member.updateBio(member, bio));
    }

    @Transactional
    public void updatePassword(Member member, String password) {
        memberRepository.save(
                Member.updatePassword(member, password));
    }

    @Transactional
    public void updateWithdrawal(Member member) {
        memberRepository.save(
                Member.withdrawal(member));
    }

    @Transactional
    public void updateSessionId(Member member, Long sessionId) {
        memberRepository.save(
                Member.updateSessionId(member, sessionId));
    }

    @Transactional
    public void updateStopStartDate(Member member, LocalDateTime date) {
        memberRepository.save(
                Member.updateStopStartDate(member, date));
    }

    @Transactional
    public void updateStopEndDate(Member member, LocalDateTime date) {
        memberRepository.save(
                Member.updateStopEndDate(member, date));
    }

    @Transactional
    public void updateStatus(Member member, int status) {
        memberRepository.save(
                Member.updateStatus(member, status));
    }

    @Transactional
    public void addReportCount(Member member, int add) {
        memberRepository.save(
                Member.updateReportCount(member, add));
    }
}
