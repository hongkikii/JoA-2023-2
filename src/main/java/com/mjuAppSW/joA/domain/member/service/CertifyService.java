package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_CERTIFY_TIME;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_EMAIL;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.BEFORE_CERTIFY_TIME;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.BEFORE_EMAIL;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.CERTIFY_NUMBER;
import static com.mjuAppSW.joA.common.constant.Constants.Mail.CERTIFY_NUMBER_IS;
import static com.mjuAppSW.joA.common.exception.BusinessException.*;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.domain.mCollege.entity.MCollege;
import com.mjuAppSW.joA.domain.mCollege.service.MCollegeQueryService;
import com.mjuAppSW.joA.domain.member.dto.request.CertifyNumSendRequest;
import com.mjuAppSW.joA.domain.member.dto.request.AsyncRequest;
import com.mjuAppSW.joA.domain.member.dto.request.CertifyNumVerifyRequest;
import com.mjuAppSW.joA.domain.member.infrastructure.CacheManager;
import com.mjuAppSW.joA.domain.member.infrastructure.MailSender;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class CertifyService {

    private final MemberQueryService memberQueryService;
    private final MCollegeQueryService mCollegeQueryService;
    private final SessionService sessionService;
    private final CacheManager cacheManager;
    private final MailSender mailSender;

    public AsyncRequest validate(CertifyNumSendRequest request) {
        MCollege college = mCollegeQueryService.getById(request.getCollegeId());
        String uEmail = request.getCollegeEmail();

        memberQueryService.validateNoExistedEmail(uEmail, college);
        memberQueryService.validateNoPermanentBan(uEmail, college);
        String eMail = uEmail + college.getDomain();

        return AsyncRequest.of(sessionService.create(), eMail);
    }

    @Async
    public void send(AsyncRequest request) {
        Long sessionId = request.getSessionId();
        cacheManager.add(BEFORE_EMAIL + sessionId, request.getEMail(), BEFORE_CERTIFY_TIME);

        String certifyNum = cacheManager.addRandomValue(CERTIFY_NUMBER + sessionId, BEFORE_CERTIFY_TIME);
        mailSender.send(request.getEMail(), CERTIFY_NUMBER_IS, certifyNum);
    }

    public void verify(CertifyNumVerifyRequest request) {
        Long sessionId = request.getId();
        sessionService.checkInCache(CERTIFY_NUMBER, sessionId);

        if (!cacheManager.compare(CERTIFY_NUMBER + sessionId, request.getCertifyNum())) {
            throw InvalidCertifyNumberException;
        }
        cacheEmailOnly(sessionId);
    }

    private void cacheEmailOnly(Long sessionId) {
        cacheManager.delete(CERTIFY_NUMBER + sessionId);
        String Email = cacheManager.delete(BEFORE_EMAIL + sessionId);
        cacheManager.add(AFTER_EMAIL + sessionId, Email, AFTER_CERTIFY_TIME);
    }
}
