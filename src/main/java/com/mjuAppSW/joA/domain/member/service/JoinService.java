package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_EMAIL;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_SAVE_LOGIN_ID_TIME;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.ID;
import static com.mjuAppSW.joA.common.constant.Constants.EMAIL_SPLIT;

import com.mjuAppSW.joA.domain.college.MCollege;
import com.mjuAppSW.joA.domain.college.MCollegeService;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.dto.request.JoinRequest;
import com.mjuAppSW.joA.domain.member.dto.request.VerifyIdRequest;
import com.mjuAppSW.joA.domain.member.exception.LoginIdNotAuthorizedException;
import com.mjuAppSW.joA.domain.member.infrastructure.CacheManager;
import com.mjuAppSW.joA.domain.member.infrastructure.LoginIdManager;
import com.mjuAppSW.joA.domain.member.infrastructure.repository.MemberRepository;
import com.mjuAppSW.joA.domain.member.infrastructure.PasswordManager;
import com.mjuAppSW.joA.geography.college.PCollege;
import com.mjuAppSW.joA.geography.college.PCollegeService;
import com.mjuAppSW.joA.geography.location.LocationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService { //FIXME

    private final MemberRepository memberRepository;
    private final LocationService locationService;
    private final MCollegeService mCollegeService;
    private final PCollegeService pCollegeService;
    private final SessionService sessionManager;
    private final CacheManager cacheManager;
    private final PasswordManager passwordManager;
    private final LoginIdManager loginIdManager;

    public void verifyLoginId(VerifyIdRequest request) {
        String loginId = request.getLoginId();
        Long sessionId = request.getSessionId();
        loginIdManager.validate(loginId);
        sessionManager.checkInCache(AFTER_EMAIL, sessionId);
        loginIdManager.checkInCache(sessionId, loginId);
        loginIdManager.checkInDb(loginId);
        cacheLoginId(sessionId, loginId);
    }

    private void cacheLoginId(Long sessionId, String loginId) {
        cacheManager.add(ID + sessionId, loginId, AFTER_SAVE_LOGIN_ID_TIME);
        cacheManager.changeTime(AFTER_EMAIL + sessionId, AFTER_SAVE_LOGIN_ID_TIME);
    }

    @Transactional
    public void join(JoinRequest request) {
        passwordManager.validate(request.getPassword());
        Long sessionId = request.getId();
        sessionManager.checkInCache(AFTER_EMAIL, sessionId);
        checkNotCachedLoginId(sessionId, request.getLoginId());

        String eMail = cacheManager.getData(AFTER_EMAIL + sessionId);
        String[] splitEMail = eMail.split(EMAIL_SPLIT);
        String uEmail = splitEMail[0];
        MCollege mCollege = mCollegeService.findByDomain(splitEMail[1]);
        PCollege pCollege = pCollegeService.findById(mCollege.getId());

        Member member = create(request, uEmail, mCollege);
        memberRepository.save(member);
        locationService.create(member, pCollege);
        emptyCache(sessionId);
    }

    private void checkNotCachedLoginId(Long sessionId, String loginId) {
        if(!cacheManager.compare(ID + sessionId, loginId)){
            throw new LoginIdNotAuthorizedException();
        }
    }

    private Member create(JoinRequest request, String uEmail, MCollege mCollege) {
        String salt = passwordManager.createSalt();
        String hashedPassword = passwordManager.createHashed(request.getPassword(), salt);
        Member member = Member.builder().name(request.getName())
                .loginId(request.getLoginId())
                .password(hashedPassword)
                .salt(salt)
                .uEmail(uEmail)
                .college(mCollege)
                .sessionId(request.getId()).build();
        memberRepository.save(member);
        return member;
    }

    private void emptyCache(Long sessionId) {
        cacheManager.delete(ID + sessionId);
        cacheManager.delete(AFTER_EMAIL + sessionId);
    }
}
