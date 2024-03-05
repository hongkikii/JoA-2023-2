package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.exception.BusinessException.*;
import static com.mjuAppSW.joA.domain.member.entity.Status.*;

import com.mjuAppSW.joA.domain.mCollege.entity.MCollege;
import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.member.infrastructure.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public Member getBySessionId(Long sessionId) {
        return memberRepository.findBysessionId(sessionId)
                .orElseThrow(() -> MemberNotFoundException);
    }

    public Member getNormalBySessionId(Long sessionId) {
        return memberRepository.findBysessionId(sessionId)
                .filter(member -> {
                    if (member.getStatus() == STEP_1_STOP
                            || member.getStatus() == STEP_2_STOP) {
                        throw AccessStoppedException;
                    }
                    if (member.getStatus() == STEP_3_STOP) {
                        throw PermanentBanException;
                    }
                    return true;
                })
                .orElseThrow(() -> MemberNotFoundException);
    }

    public Member getById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> MemberNotFoundException);
    }

    public Member getByLoginId(String loginId) {
        return memberRepository.findByloginId(loginId)
                .orElseThrow(() -> MemberNotFoundException);
    }

    public Member getByUEmailAndCollege(String uEmail, MCollege mCollege) {
        return memberRepository.findByuEmailAndcollege(uEmail, mCollege)
                .orElseThrow(() -> MemberNotFoundException);
    }

    public void validateNoExistedEmail(String uEmail, MCollege college) {
        memberRepository.findByuEmailAndcollege(uEmail, college)
                .ifPresent(member -> {
                    throw MemberAlreadyExistedException;});
    }

    public void validateNoExistedLoginId(String loginId) {
        memberRepository.findByloginId(loginId)
                .ifPresent(existingMember -> {
                    throw LoginIdAlreadyExistedException;});
    }

    public void validateNoPermanentBan(String uEmail, MCollege mCollege) {
        memberRepository.findForbidden(uEmail, mCollege)
                .ifPresent(forbiddenMember -> {
                    throw PermanentBanException;});
    }

    public void validateNoTemporaryBan(Member member) {
        if (member.getStatus() == STEP_1_STOP
                || member.getStatus() == STEP_2_STOP) {
            throw AccessStoppedException;
        }
        if(member.getStatus() == STEP_3_STOP) {
            throw PermanentBanException;
        }
    }

    public Boolean validateIsWithDrawal(Long id){
        return memberRepository.findById(id).isPresent();
    }
}
