package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.Mail.TEMPORARY_PASSWORD_IS;
import static com.mjuAppSW.joA.common.constant.Constants.Mail.USER_ID_IS;

import com.mjuAppSW.joA.domain.mCollege.entity.MCollege;
import com.mjuAppSW.joA.domain.mCollege.service.MCollegeQueryService;
import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.member.dto.request.LoginRequest;
import com.mjuAppSW.joA.domain.member.dto.request.PasswordRequest;
import com.mjuAppSW.joA.domain.member.dto.response.SessionIdResponse;
import com.mjuAppSW.joA.domain.member.infrastructure.MailSender;
import com.mjuAppSW.joA.domain.member.infrastructure.PasswordManager;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class AccountService {

    private final MemberService memberService;
    private final MemberQueryService memberQueryService;
    private final MCollegeQueryService mCollegeQueryService;
    private final SessionService sessionService;
    private final MailSender mailSender;
    private final PasswordManager passwordManager;

    @Transactional
    public SessionIdResponse login(LoginRequest request) {
        Member member = memberQueryService.getByLoginId(request.getLoginId());
        String hashedPassword = passwordManager.createHashed (
                request.getPassword(), member.getSalt());
        passwordManager.compare(member.getPassword(), hashedPassword);
        member.updateSessionId(sessionService.create());
        member.updateFcmToken(request.getFcmToken());
        return SessionIdResponse.of(member.getSessionId());
    }

    @Transactional
    public void logout(Long sessionId) {
        Member member = memberQueryService.getBySessionId(sessionId);
        member.expireSessionId();
    }

    public void findLoginId(String collegeEmail, Long collegeId) {
        MCollege college = mCollegeQueryService.getById(collegeId);
        Member member = memberQueryService.getByUEmailAndCollege(collegeEmail, college);

        String email = member.getUEmail() + college.getDomain();
        mailSender.send(email, USER_ID_IS, member.getLoginId());
    }

    @Transactional
    public void findPassword(String loginId) {
        Member member = memberQueryService.getByLoginId(loginId);

        String randomPassword = passwordManager.createRandom();
        String hashedRandomPassword = passwordManager.createHashed(
                randomPassword, member.getSalt());

        String email = member.getUEmail() + member.getCollege().getDomain();
        mailSender.send(email, TEMPORARY_PASSWORD_IS, randomPassword);
        member.updatePassword(hashedRandomPassword);
    }

    @Transactional
    public void transPassword(PasswordRequest request) {
        Member member = memberQueryService.getBySessionId(request.getId());

        String hashedCurrentPassword = passwordManager.createHashed(
                request.getCurrentPassword(), member.getSalt());
        passwordManager.compare(member.getPassword(), hashedCurrentPassword);

        passwordManager.validate(request.getNewPassword());
        String hashedNewPassword = passwordManager.createHashed(
                request.getNewPassword(), member.getSalt());
        member.updatePassword(hashedNewPassword);
    }

    @Transactional
    public void withdrawal(Long sessionId) {
        Member member = memberQueryService.getBySessionId(sessionId);
        memberService.delete(member);
    }
}
