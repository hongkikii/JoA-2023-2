package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.EMPTY_STRING;
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
import com.mjuAppSW.joA.domain.member.exception.PasswordNotFoundException;
import com.mjuAppSW.joA.domain.member.infrastructure.MailSender;
import com.mjuAppSW.joA.domain.member.infrastructure.MemberRepository;
import com.mjuAppSW.joA.domain.member.infrastructure.PasswordManager;
import com.mjuAppSW.joA.domain.member.service.port.S3Uploader;
import com.mjuAppSW.joA.domain.member.exception.InvalidS3Exception;
import com.mjuAppSW.joA.domain.member.exception.MemberNotFoundException;
import com.mjuAppSW.joA.geography.location.LocationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final LocationService locationService;
    private final MCollegeService mCollegeService;
    private final SessionService sessionManager;
    private final MailSender mailSender;
    private final S3Uploader s3Uploader;
    private final PasswordManager passwordManager;

    @Transactional
    public SessionIdResponse login(LoginRequest request) {
        Member findMember = memberService.findByLoginId(request.getLoginId());
        findMember.makeSessionId(sessionManager.create());
        String hashedPassword = BCrypt.hashpw(request.getPassword(), findMember.getSalt());

        compare(findMember.getPassword(), hashedPassword);
        return SessionIdResponse.of(findMember.getSessionId());
    }

    private void compare(String password, String hashedPassword) {
        if (!password.equals(hashedPassword)) {
            throw new PasswordNotFoundException();
        }
    }

    @Transactional
    public void logout(Long sessionId) {
        Member findMember = memberService.findBySessionId(sessionId);
        locationService.updateIsContained(findMember.getId(), false);
        findMember.expireSessionId();
    }

    public void findLoginId(FindIdRequest request) {
        MCollegeEntity college = mCollegeService.findById(request.getCollegeId());

        Member member = memberRepository.findByuEmailAndcollege(request.getCollegeEmail(), college)
                .orElseThrow(MemberNotFoundException::new);

        String email = member.getUEmail() + college.getDomain();
        mailSender.send(email, USER_ID_IS, member.getLoginId());
    }

    @Transactional
    public void findPassword(FindPasswordRequest request) {
        Member member = memberService.findByLoginId(request.getLoginId());

        String randomPassword = passwordManager.createRandom();
        String hashedRandomPassword = BCrypt.hashpw(randomPassword, member.getSalt());

        String email = member.getUEmail() + member.getCollege().getDomain();
        mailSender.send(email, TEMPORARY_PASSWORD_IS, randomPassword);
        member.changePassword(hashedRandomPassword);
    }

    @Transactional
    public void transPassword(TransPasswordRequest request) {
        Member findMember = memberService.findBySessionId(request.getId());

        String hashedCurrentPassword = BCrypt.hashpw(request.getCurrentPassword(), findMember.getSalt());
        if (findMember.getPassword().equals(hashedCurrentPassword)) {
            passwordManager.validate(request.getNewPassword());
            String hashedNewPassword = BCrypt.hashpw(request.getNewPassword(), findMember.getSalt());
            findMember.changePassword(hashedNewPassword);
            return;
        }
        throw new PasswordNotFoundException();
    }

    @Transactional
    public void withdrawal(Long sessionId) {
        Member member = memberService.findBySessionId(sessionId);

        if (s3Uploader.deletePicture(member.getUrlCode())) {
            locationService.delete(member.getId());
            member.expireSessionId();
            member.changeWithdrawal(true);
            member.changeUrlCode(EMPTY_STRING);
            return;
        }
        throw new InvalidS3Exception();
    }
}
