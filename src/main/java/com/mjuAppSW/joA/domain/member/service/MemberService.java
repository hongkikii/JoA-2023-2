package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.MemberStatus.STEP_1_STOP_STATUS;
import static com.mjuAppSW.joA.common.constant.Constants.MemberStatus.STEP_2_STOP_STATUS;
import static com.mjuAppSW.joA.common.constant.Constants.MemberStatus.STEP_3_STOP_STATUS;

import com.mjuAppSW.joA.domain.college.MCollege;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.exception.LoginIdAlreadyExistedException;
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

    public Member getBySessionId(Long sessionId) {
        return memberRepository.findBysessionId(sessionId)
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member getNormalBySessionId(Long sessionId) {
        return memberRepository.findBysessionId(sessionId)
                .filter(member -> {
                    if (member.getStatus() == STEP_1_STOP_STATUS
                        || member.getStatus() == STEP_2_STOP_STATUS) {
                        throw new AccessStoppedException();
                    }
                    if (member.getStatus() == STEP_3_STOP_STATUS) {
                        throw new PermanentBanException();
                    }
                    return true;
                })
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member getById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member getByLoginId(String loginId) {
        return memberRepository.findByloginId(loginId)
                .orElseThrow(MemberNotFoundException::new);
    }

    public Member getByUEmailAndCollege(String uEmail, MCollege mCollege) {
        return memberRepository.findByuEmailAndcollege(uEmail, mCollege)
                .orElseThrow(MemberNotFoundException::new);
    }

    public void checkExist(String uEmail, MCollege college) {
        memberRepository.findByuEmailAndcollege(uEmail, college)
                .ifPresent(member -> {
                    throw new MemberAlreadyExistedException();});
    }

    public void checkForbidden(String uEmail, MCollege mCollege) {
        memberRepository.findForbidden(uEmail, mCollege)
                .ifPresent(forbiddenMember -> {
                    throw new PermanentBanException();});
    }

    public void checkExistedLoginId(String loginId) {
        memberRepository.findByloginId(loginId)
                .ifPresent(existingMember -> {
                    throw new LoginIdAlreadyExistedException();});
    }

    public void checkStopped(Member member) {
        if (member.getStatus() == STEP_1_STOP_STATUS
            || member.getStatus() == STEP_2_STOP_STATUS) {
            throw new AccessStoppedException();
        }
        if(member.getStatus() == STEP_3_STOP_STATUS) {
            throw new PermanentBanException();
        }
    }
}
