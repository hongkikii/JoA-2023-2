package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.MAIL.TEMPORARY_PASSWORD_IS;
import static com.mjuAppSW.joA.common.constant.Constants.MAIL.USER_ID_IS;

import com.mjuAppSW.joA.domain.college.MCollegeEntity;
import com.mjuAppSW.joA.domain.college.MCollegeService;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.dto.request.FindIdRequest;
import com.mjuAppSW.joA.domain.member.dto.request.FindPasswordRequest;
import com.mjuAppSW.joA.domain.member.dto.request.LoginRequest;
import com.mjuAppSW.joA.domain.member.dto.request.TransPasswordRequest;
import com.mjuAppSW.joA.domain.member.dto.response.SessionIdResponse;
import com.mjuAppSW.joA.domain.member.infrastructure.ImageUploader;
import com.mjuAppSW.joA.domain.member.infrastructure.MailSender;
import com.mjuAppSW.joA.domain.member.infrastructure.PasswordManager;
import com.mjuAppSW.joA.geography.location.LocationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberService memberService;
    private final LocationService locationService;
    private final MCollegeService mCollegeService;
    private final SessionService sessionManager;
    private final MailSender mailSender;
    private final ImageUploader imageUploader;
    private final PasswordManager passwordManager;

    @Transactional
    public SessionIdResponse login(LoginRequest request) {
        Member member = memberService.findByLoginId(request.getLoginId());
        String hashedPassword = passwordManager.createHashed (
                request.getPassword(), member.getSalt());
        passwordManager.compare(member.getPassword(), hashedPassword);
        memberService.updateSessionId(member, sessionManager.create());
        return SessionIdResponse.of(member.getSessionId());
    }

    @Transactional
    public void logout(Long sessionId) {
        Member member = memberService.findBySessionId(sessionId);
        locationService.updateIsContained(member.getId(), false);
        memberService.updateSessionId(member, null);
    }

    public void findLoginId(FindIdRequest request) {
        MCollegeEntity college = mCollegeService.findById(request.getCollegeId());
        Member member = memberService.findByUEmailAndCollege(request.getCollegeEmail(), college);

        String email = member.getUEmail() + college.getDomain();
        mailSender.send(email, USER_ID_IS, member.getLoginId());
    }

    @Transactional
    public void findPassword(FindPasswordRequest request) {
        Member member = memberService.findByLoginId(request.getLoginId());

        String randomPassword = passwordManager.createRandom();
        String hashedRandomPassword = passwordManager.createHashed(
                randomPassword, member.getSalt());

        String email = member.getUEmail() + member.getCollege().getDomain();
        mailSender.send(email, TEMPORARY_PASSWORD_IS, randomPassword);
        memberService.updatePassword(member, hashedRandomPassword);
    }

    @Transactional
    public void transPassword(TransPasswordRequest request) {
        Member member = memberService.findBySessionId(request.getId());

        String hashedCurrentPassword = passwordManager.createHashed(
                request.getCurrentPassword(), member.getSalt());
        passwordManager.compare(member.getPassword(), hashedCurrentPassword);

        passwordManager.validate(request.getNewPassword());
        String hashedNewPassword = passwordManager.createHashed(
                request.getNewPassword(), member.getSalt());
        memberService.updatePassword(member, hashedNewPassword);
    }

    @Transactional
    public void withdrawal(Long sessionId) {
        Member member = memberService.findBySessionId(sessionId);

        imageUploader.delete(member.getUrlCode());
        locationService.delete(member.getId());
        memberService.updateWithdrawal(member);
    }
}
