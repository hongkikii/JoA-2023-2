package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_EMAIL;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_SAVE_LOGIN_ID_TIME;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.ID;
import static com.mjuAppSW.joA.common.constant.Constants.EMAIL_SPLIT;

import com.mjuAppSW.joA.domain.college.MCollege;
import com.mjuAppSW.joA.domain.college.MCollegeService;
import com.mjuAppSW.joA.domain.member.dto.request.JoinRequest;
import com.mjuAppSW.joA.domain.member.dto.request.VerifyIdRequest;
import com.mjuAppSW.joA.domain.member.infrastructure.CacheManager;
import com.mjuAppSW.joA.domain.member.infrastructure.LoginIdManager;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class JoinService { //FIXME

    private final MemberService memberService;
    private final MCollegeService mCollegeService;
    private final SessionService sessionService;
    private final CacheManager cacheManager;
    private final LoginIdManager loginIdManager;

    public void verifyLoginId(VerifyIdRequest request) {
        String loginId = request.getLoginId();
        Long sessionId = request.getSessionId();
        loginIdManager.validate(loginId);
        sessionService.checkInCache(AFTER_EMAIL, sessionId);
        loginIdManager.checkInCache(sessionId, loginId);
        loginIdManager.checkInDb(loginId);
        cacheLoginId(sessionId, loginId);
    }

    private void cacheLoginId(Long sessionId, String loginId) {
        cacheManager.add(ID + sessionId, loginId, AFTER_SAVE_LOGIN_ID_TIME);
        cacheManager.changeTime(AFTER_EMAIL + sessionId, AFTER_SAVE_LOGIN_ID_TIME);
    }

    @Transactional
    public void execute(JoinRequest request) {
        Long sessionId = request.getId();
        sessionService.checkInCache(AFTER_EMAIL, sessionId);
        loginIdManager.checkNotCache(sessionId, request.getLoginId());

        String eMail = cacheManager.getData(AFTER_EMAIL + sessionId);
        String[] splitEMail = eMail.split(EMAIL_SPLIT);
        String uEmail = splitEMail[0];
        MCollege mCollege = mCollegeService.getByDomain(splitEMail[1]);

        memberService.create(sessionId, request.getName(), request.getLoginId(),
                            request.getPassword(), uEmail, mCollege);
        emptyCache(sessionId);
    }

    private void emptyCache(Long sessionId) {
        cacheManager.delete(ID + sessionId);
        cacheManager.delete(AFTER_EMAIL + sessionId);
    }
}
