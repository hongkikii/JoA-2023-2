package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_CERTIFY_TIME;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_EMAIL;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.BEFORE_CERTIFY_TIME;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.BEFORE_EMAIL;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.CERTIFY_NUMBER;
import static com.mjuAppSW.joA.common.constant.Constants.Mail.CERTIFY_NUMBER_IS;

import com.mjuAppSW.joA.domain.college.MCollege;
import com.mjuAppSW.joA.domain.college.MCollegeService;
import com.mjuAppSW.joA.domain.member.dto.request.SendCertifyNumRequest;
import com.mjuAppSW.joA.domain.member.dto.request.VerifyCertifyNumRequest;
import com.mjuAppSW.joA.domain.member.dto.response.SessionIdResponse;
import com.mjuAppSW.joA.domain.member.exception.InvalidCertifyNumberException;
import com.mjuAppSW.joA.domain.member.exception.JoiningMailException;
import com.mjuAppSW.joA.domain.member.infrastructure.CacheManager;
import com.mjuAppSW.joA.domain.member.infrastructure.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertifyService { //FIXME

    private final MemberService memberService;
    private final MCollegeService mCollegeService;
    private final SessionService sessionManager;
    private final CacheManager cacheManager;
    private final MailSender mailSender;

    public SessionIdResponse send(SendCertifyNumRequest request) {
        MCollege college = mCollegeService.findById(request.getCollegeId());
        String uEmail = request.getCollegeEmail();

        memberService.checkExist(uEmail, college);
        memberService.checkForbidden(uEmail, college);
        String eMail = uEmail + college.getDomain();
        checkJoining(eMail);

        long sessionId = sessionManager.create();
        String certifyNum = cacheManager.addRandomValue(
                CERTIFY_NUMBER + sessionId, BEFORE_CERTIFY_TIME);
        cacheManager.add(BEFORE_EMAIL + sessionId, eMail, BEFORE_CERTIFY_TIME);

        mailSender.send(eMail, CERTIFY_NUMBER_IS,certifyNum);
        return SessionIdResponse.of(sessionId);
    }

    private void checkJoining(String eMail) {
        if (cacheManager.isExistedValue(BEFORE_EMAIL, eMail)
                || cacheManager.isExistedValue(AFTER_EMAIL, eMail)) {
            throw new JoiningMailException();
        }
    }

    public void verify(VerifyCertifyNumRequest request) {
        Long sessionId = request.getId();
        sessionManager.checkInCache(CERTIFY_NUMBER, sessionId);

        if (!cacheManager.compare(CERTIFY_NUMBER + sessionId, request.getCertifyNum())) {
            throw new InvalidCertifyNumberException();
        }
        cacheEmailOnly(sessionId);
    }

    private void cacheEmailOnly(Long sessionId) {
        cacheManager.delete(CERTIFY_NUMBER + sessionId);
        String Email = cacheManager.delete(BEFORE_EMAIL + sessionId);
        cacheManager.add(AFTER_EMAIL + sessionId, Email, AFTER_CERTIFY_TIME);
    }
}
