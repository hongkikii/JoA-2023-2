package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_EMAIL;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_SAVE_LOGIN_ID_TIME;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.ID;
import static com.mjuAppSW.joA.common.constant.Constants.EMAIL_SPLIT;

import com.mjuAppSW.joA.domain.college.MCollegeEntity;
import com.mjuAppSW.joA.domain.college.MCollegeService;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.dto.request.JoinRequest;
import com.mjuAppSW.joA.domain.member.dto.request.VerifyIdRequest;
import com.mjuAppSW.joA.domain.member.exception.InvalidLoginIdException;
import com.mjuAppSW.joA.domain.member.exception.LoginIdAlreadyExistedException;
import com.mjuAppSW.joA.domain.member.exception.LoginIdNotAuthorizedException;
import com.mjuAppSW.joA.domain.member.infrastructure.MemberRepository;
import com.mjuAppSW.joA.domain.member.infrastructure.PasswordManager;
import com.mjuAppSW.joA.domain.member.service.port.CacheManager;
import com.mjuAppSW.joA.geography.college.PCollege;
import com.mjuAppSW.joA.geography.college.PCollegeService;
import com.mjuAppSW.joA.geography.location.LocationService;
import jakarta.transaction.Transactional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final MemberRepository memberRepository;
    private final LocationService locationService;
    private final MCollegeService mCollegeService;
    private final PCollegeService pCollegeService;
    private final SessionService sessionManager;
    private final CacheManager cacheManager;
    private final PasswordManager passwordManager;

    public void verifyLoginId(VerifyIdRequest request) {
        String loginId = request.getLoginId();
        Long sessionId = request.getSessionId();
        validateLoginId(loginId);
        sessionManager.isCached(AFTER_EMAIL, sessionId);
        checkExistedLoginId(sessionId, loginId);
        cacheLoginId(sessionId, loginId);
    }

    private void validateLoginId(String id) {
        if (id.length() < 5 || id.length() > 20) {
            throw new InvalidLoginIdException();
        }
        String regex = "^[a-z0-9-_]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(id);
        if(!matcher.matches()){
            throw new InvalidLoginIdException();
        }
    }

    private void checkExistedLoginId(Long sessionId, String loginId) {
        memberRepository.findByloginId(loginId).ifPresent(existingMember -> {
            throw new LoginIdAlreadyExistedException();});

        if (cacheManager.isExistedValue(ID, loginId)) {
            if(!cacheManager.compare(ID + sessionId, loginId)) {
                throw new LoginIdAlreadyExistedException();
            }
        }
    }

    private void cacheLoginId(Long sessionId, String loginId) {
        cacheManager.add(ID + sessionId, loginId, AFTER_SAVE_LOGIN_ID_TIME);
        cacheManager.changeTime(AFTER_EMAIL + sessionId, AFTER_SAVE_LOGIN_ID_TIME);
    }

    @Transactional
    public void join(JoinRequest request) {
        passwordManager.validate(request.getPassword());
        Long sessionId = request.getId();
        sessionManager.isCached(AFTER_EMAIL, sessionId);
        checkNotCachedLoginId(sessionId, request.getLoginId());

        String eMail = cacheManager.getData(AFTER_EMAIL + sessionId);
        String[] splitEMail = eMail.split(EMAIL_SPLIT);
        String uEmail = splitEMail[0];
        MCollegeEntity mCollegeEntity = mCollegeService.findByDomain(splitEMail[1]);
        PCollege pCollege = pCollegeService.findById(mCollegeEntity.getId());

        Member joinMember = createMember(request, uEmail, mCollegeEntity);
        locationService.create(joinMember, pCollege);
        emptyCache(sessionId);
    }

    private void checkNotCachedLoginId(Long sessionId, String loginId) {
        if(!cacheManager.compare(ID + sessionId, loginId)){
            throw new LoginIdNotAuthorizedException();
        }
    }

    private Member createMember(JoinRequest request, String uEmail, MCollegeEntity mCollegeEntity) {
        String salt = passwordManager.createSalt();
        String hashedPassword = passwordManager.createHashed(request.getPassword(), salt);

        Member joinMember = Member.builder().name(request.getName())
                .loginId(request.getLoginId())
                .password(hashedPassword)
                .salt(salt)
                .uEmail(uEmail)
                .college(mCollegeEntity)
                .sessionId(request.getId()).build();
        memberRepository.save(joinMember);
        return joinMember;
    }

    private void emptyCache(Long sessionId) {
        cacheManager.delete(ID + sessionId);
        cacheManager.delete(AFTER_EMAIL + sessionId);
    }
}
