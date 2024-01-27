package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_EMAIL;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_SAVE_LOGIN_ID_TIME;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.ID;
import static com.mjuAppSW.joA.common.constant.Constants.EMAIL_SPLIT;
import static com.mjuAppSW.joA.common.constant.Constants.EMPTY_STRING;
import static com.mjuAppSW.joA.common.constant.Constants.MAIL.TEMPORARY_PASSWORD_IS;
import static com.mjuAppSW.joA.common.constant.Constants.MAIL.USER_ID_IS;

import com.mjuAppSW.joA.common.auth.MemberChecker;
import com.mjuAppSW.joA.domain.college.MCollegeEntity;
import com.mjuAppSW.joA.domain.college.MCollegeService;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.dto.request.FindIdRequest;
import com.mjuAppSW.joA.domain.member.dto.request.FindPasswordRequest;
import com.mjuAppSW.joA.domain.member.dto.request.JoinRequest;
import com.mjuAppSW.joA.domain.member.dto.request.LoginRequest;
import com.mjuAppSW.joA.domain.member.dto.request.TransPasswordRequest;
import com.mjuAppSW.joA.domain.member.dto.request.VerifyIdRequest;
import com.mjuAppSW.joA.domain.member.dto.response.SessionIdResponse;
import com.mjuAppSW.joA.domain.member.exception.InvalidLoginIdException;
import com.mjuAppSW.joA.domain.member.exception.InvalidPasswordException;
import com.mjuAppSW.joA.domain.member.exception.LoginIdAlreadyExistedException;
import com.mjuAppSW.joA.domain.member.exception.LoginIdNotAuthorizedException;
import com.mjuAppSW.joA.domain.member.exception.PasswordNotFoundException;
import com.mjuAppSW.joA.domain.member.infrastructure.MailSender;
import com.mjuAppSW.joA.domain.member.infrastructure.MemberRepository;
import com.mjuAppSW.joA.domain.memberProfile.exception.MemberNotFoundException;
import com.mjuAppSW.joA.domain.memberProfile.exception.InvalidS3Exception;
import com.mjuAppSW.joA.geography.college.PCollege;
import com.mjuAppSW.joA.geography.college.PCollegeRepository;
import com.mjuAppSW.joA.common.auth.SessionManager;
import com.mjuAppSW.joA.domain.member.service.port.CacheManager;
import com.mjuAppSW.joA.domain.member.service.port.S3Uploader;
import com.mjuAppSW.joA.geography.college.PCollegeService;
import com.mjuAppSW.joA.geography.location.LocationService;
import com.mjuAppSW.joA.geography.location.exception.CollegeNotFoundException;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PCollegeService pCollegeService;
    private final LocationService locationService;
    private final MCollegeService mCollegeService;
    private final SessionManager sessionManager;
    private final MailSender mailSender;
    private final CacheManager cacheManager;
    private final S3Uploader s3Uploader;
    private final MemberChecker memberChecker;

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
        validatePassword(request.getPassword());
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

    private static void validatePassword(String password) {
        String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=]).{8,16}$";
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(password);
        if (!matcher.matches()) {
            throw new InvalidPasswordException();
        }
    }

    private void checkNotCachedLoginId(Long sessionId, String loginId) {
        if(!cacheManager.compare(ID + sessionId, loginId)){
            throw new LoginIdNotAuthorizedException();
        }
    }

    private Member createMember(JoinRequest request, String uEmail, MCollegeEntity mCollegeEntity) {
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(request.getPassword(), salt);

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

    @Transactional
    public SessionIdResponse login(LoginRequest request) {
        Member findMember = memberChecker.findByLoginId(request.getLoginId());
        findMember.makeSessionId(sessionManager.makeSessionId());
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
        Member findMember = memberChecker.findBySessionId(sessionId);
        locationService.updateIsContained(findMember.getId(), false);
        findMember.expireSessionId();
    }

    public void findId(FindIdRequest request) {
        MCollegeEntity college = mCollegeService.findById(request.getCollegeId());

        Member member = memberRepository.findByuEmailAndcollege(request.getCollegeEmail(), college)
                .orElseThrow(MemberNotFoundException::new);

        String email = member.getUEmail() + college.getDomain();
        mailSender.send(email, USER_ID_IS, member.getLoginId());
    }

    @Transactional
    public void findPassword(FindPasswordRequest request) {
        Member member = memberChecker.findByLoginId(request.getLoginId());

        String randomPassword = randomPassword();
        String hashedRandomPassword = BCrypt.hashpw(randomPassword, member.getSalt());

        String email = member.getUEmail() + member.getCollege().getDomain();
        mailSender.send(email, TEMPORARY_PASSWORD_IS, randomPassword);
        member.changePassword(hashedRandomPassword);
    }

    private String randomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase();
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()-_+=<>?";

        String allCharacters = upper + lower + digits + specialChars;
        int maxLength = 16;
        Random random = new SecureRandom();
        StringBuilder builder = new StringBuilder();

        while (builder.length() < maxLength) {
            int index = random.nextInt(allCharacters.length());
            char randomChar = allCharacters.charAt(index);
            builder.append(randomChar);
        }
        return builder.toString();
    }

    @Transactional
    public void transPassword(TransPasswordRequest request) {
        Member findMember = memberChecker.findBySessionId(request.getId());

        String hashedCurrentPassword = BCrypt.hashpw(request.getCurrentPassword(), findMember.getSalt());
        if (findMember.getPassword().equals(hashedCurrentPassword)) {
            validatePassword(request.getNewPassword());
            String hashedNewPassword = BCrypt.hashpw(request.getNewPassword(), findMember.getSalt());
            findMember.changePassword(hashedNewPassword);
            return;
        }
        throw new PasswordNotFoundException();
    }

    @Transactional
    public void withdrawal(Long sessionId) {
        Member member = memberChecker.findBySessionId(sessionId);

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
