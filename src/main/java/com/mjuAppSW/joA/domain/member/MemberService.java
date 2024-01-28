package com.mjuAppSW.joA.domain.member;

import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_CERTIFY_TIME;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_EMAIL;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.AFTER_SAVE_LOGIN_ID_TIME;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.BEFORE_CERTIFY_TIME;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.BEFORE_EMAIL;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.CERTIFY_NUMBER;
import static com.mjuAppSW.joA.common.constant.Constants.Cache.ID;
import static com.mjuAppSW.joA.common.constant.Constants.EMAIL_SPLIT;
import static com.mjuAppSW.joA.common.constant.Constants.EMPTY_STRING;
import static com.mjuAppSW.joA.common.constant.Constants.MAIL.CERTIFY_NUMBER_IS;
import static com.mjuAppSW.joA.common.constant.Constants.MAIL.TEMPORARY_PASSWORD_IS;
import static com.mjuAppSW.joA.common.constant.Constants.MAIL.USER_ID_IS;

import com.mjuAppSW.joA.common.auth.MemberChecker;
import com.mjuAppSW.joA.domain.college.MCollege;
import com.mjuAppSW.joA.domain.college.MCollegeRepository;
import com.mjuAppSW.joA.domain.member.dto.request.FindIdRequest;
import com.mjuAppSW.joA.domain.member.dto.request.FindPasswordRequest;
import com.mjuAppSW.joA.domain.member.dto.request.JoinRequest;
import com.mjuAppSW.joA.domain.member.dto.request.LoginRequest;
import com.mjuAppSW.joA.domain.member.dto.request.TransPasswordRequest;
import com.mjuAppSW.joA.domain.member.dto.request.SendCertifyNumRequest;
import com.mjuAppSW.joA.domain.member.dto.request.VerifyIdRequest;
import com.mjuAppSW.joA.domain.member.dto.response.SessionIdResponse;
import com.mjuAppSW.joA.domain.member.dto.request.VerifyCertifyNumRequest;
import com.mjuAppSW.joA.domain.member.exception.InvalidCertifyNumberException;
import com.mjuAppSW.joA.domain.member.exception.InvalidLoginIdException;
import com.mjuAppSW.joA.domain.member.exception.InvalidPasswordException;
import com.mjuAppSW.joA.domain.member.exception.JoiningMailException;
import com.mjuAppSW.joA.domain.member.exception.LoginIdAlreadyExistedException;
import com.mjuAppSW.joA.domain.member.exception.LoginIdNotAuthorizedException;
import com.mjuAppSW.joA.domain.member.exception.MemberAlreadyExistedException;
import com.mjuAppSW.joA.domain.member.exception.PasswordNotFoundException;
import com.mjuAppSW.joA.domain.member.exception.PermanentBanException;
import com.mjuAppSW.joA.domain.member.exception.SessionNotFoundException;
import com.mjuAppSW.joA.domain.memberProfile.exception.MemberNotFoundException;
import com.mjuAppSW.joA.domain.memberProfile.exception.InvalidS3Exception;
import com.mjuAppSW.joA.geography.college.PCollege;
import com.mjuAppSW.joA.geography.college.PCollegeRepository;
import com.mjuAppSW.joA.geography.location.Location;
import com.mjuAppSW.joA.geography.location.LocationRepository;
import com.mjuAppSW.joA.common.auth.SessionManager;
import com.mjuAppSW.joA.common.storage.CacheManager;
import com.mjuAppSW.joA.common.storage.S3Uploader;
import com.mjuAppSW.joA.geography.location.exception.CollegeNotFoundException;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final MCollegeRepository mCollegeRepository;
    private final PCollegeRepository pCollegeRepository;
    private final LocationRepository locationRepository;
    private final SessionManager sessionManager;
    private final JavaMailSender javaMailSender;
    private final CacheManager cacheManager;
    private final S3Uploader s3Uploader;
    private final MemberChecker memberChecker;

    public SessionIdResponse sendCertifyNum(SendCertifyNumRequest request) {
        MCollege college = findByMCollegeId(request.getCollegeId());
        String uEmail = request.getCollegeEmail();

        checkExistedMember(uEmail, college);
        checkForbiddenMail(uEmail, college);
        String eMail = uEmail + college.getDomain();
        checkJoiningMail(eMail);

        long sessionId = sessionManager.makeSessionId();
        String certifyNum = cacheCertifyNumAndEmail(sessionId, eMail);
        sendCertifyNumMail(request.getCollegeEmail(), college.getDomain(), certifyNum);
        return SessionIdResponse.of(sessionId);
    }

    private MCollege findByMCollegeId(Long collegeId) {
        return mCollegeRepository.findById(collegeId)
                .orElseThrow(CollegeNotFoundException::new);
    }

    private void checkForbiddenMail(String uEmail, MCollege mCollege) {
        memberRepository.findForbidden(uEmail, mCollege)
                .ifPresent(forbiddenMember -> {
                    throw new PermanentBanException();});
    }

    private void checkExistedMember(String uEmail, MCollege college) {
        memberRepository.findByuEmailAndcollege(uEmail, college)
                .ifPresent(member -> {
                    throw new MemberAlreadyExistedException();});
    }

    private void checkJoiningMail(String eMail) {
        if (cacheManager.isExistedValue(BEFORE_EMAIL, eMail) || cacheManager.isExistedValue(AFTER_EMAIL, eMail)) {
            throw new JoiningMailException();
        }
    }

    private String cacheCertifyNumAndEmail(Long sessionId, String totalEmail) {
        String certifyNum = cacheManager.addRandomValue(CERTIFY_NUMBER + sessionId, BEFORE_CERTIFY_TIME);
        cacheManager.add(BEFORE_EMAIL + sessionId, totalEmail, BEFORE_CERTIFY_TIME);
        return certifyNum;
    }

    private void sendCertifyNumMail(String uEmail, String domain, String certifyNum) {
        mail(CERTIFY_NUMBER_IS, EMPTY_STRING, uEmail, domain, certifyNum);
    }

    private void mail(String header, String memberName, String uEmail, String collegeDomain, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(uEmail + collegeDomain);

        if (!memberName.equals(EMPTY_STRING)) {
            memberName += "님의 ";
        }
        message.setSubject("[JoA] " + memberName + header + "를 확인하세요.");
        message.setText(memberName + header + "는 " + content + " 입니다.");
        javaMailSender.send(message);
    }

    public void verifyCertifyNum(VerifyCertifyNumRequest request) {
        Long sessionId = request.getId();
        validateSession(CERTIFY_NUMBER, sessionId);
        verify(sessionId, request.getCertifyNum());
        cacheEmailOnly(sessionId);
    }

    private void validateSession(String key, Long sessionId) {
        if (cacheManager.isNotExistedKey(key + sessionId)) {
            throw new SessionNotFoundException();
        }
    }

    private void verify(Long sessionId, String certifyNum) {
        if (!cacheManager.compare(CERTIFY_NUMBER + sessionId, certifyNum)) {
            throw new InvalidCertifyNumberException();
        }
    }

    private void cacheEmailOnly(Long sessionId) {
        cacheManager.delete(CERTIFY_NUMBER + sessionId);
        String Email = cacheManager.delete(BEFORE_EMAIL + sessionId);
        cacheManager.add(AFTER_EMAIL + sessionId, Email, AFTER_CERTIFY_TIME);
    }

    public void verifyId(VerifyIdRequest request) {
        String loginId = request.getLoginId();
        Long sessionId = request.getSessionId();
        validateLoginId(loginId);
        validateSession(AFTER_EMAIL, sessionId);
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
        validateSession(AFTER_EMAIL, sessionId);
        checkNotCachedLoginId(sessionId, request.getLoginId());

        String eMail = cacheManager.getData(AFTER_EMAIL + sessionId);
        String[] splitEMail = eMail.split(EMAIL_SPLIT);
        String uEmail = splitEMail[0];
        MCollege mCollege = findByDomain(splitEMail[1]);
        PCollege pCollege = findByPCollegeId(mCollege.getId());

        Member joinMember = createMember(request, uEmail, mCollege);
        createLocation(joinMember, pCollege);
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

    private MCollege findByDomain(String domain) {
        return mCollegeRepository.findBydomain(EMAIL_SPLIT + domain)
                .orElseThrow(CollegeNotFoundException::new);
    }

    private PCollege findByPCollegeId(Long collegeId) {
        return pCollegeRepository.findById(collegeId)
                .orElseThrow(CollegeNotFoundException::new);
    }

    private Member createMember(JoinRequest request, String uEmail, MCollege mCollege) {
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(request.getPassword(), salt);

        Member joinMember = Member.builder().name(request.getName())
                                    .loginId(request.getLoginId())
                                    .password(hashedPassword)
                                    .salt(salt)
                                    .uEmail(uEmail)
                                    .college(mCollege)
                                    .sessionId(request.getId()).build();
        memberRepository.save(joinMember);
        return joinMember;
    }

    private void createLocation(Member joinMember, PCollege pCollege) {
        Location joinLocation = new Location(joinMember.getId(), pCollege);
        locationRepository.save(joinLocation);
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
        locationRepository.findById(findMember.getId())
                .ifPresent(location -> {
                    locationRepository.save(Location.builder().id(location.getId())
                                                        .college(location.getCollege())
                                                        .point(location.getPoint())
                                                        .isContained(false)
                                                        .updateDate(location.getUpdateDate()).build());});
        findMember.expireSessionId();
    }

    public void findId(String collegeEmail, Long collegeId) {
        MCollege college = findByMCollegeId(collegeId);

        Member member = memberRepository.findByuEmailAndcollege(collegeEmail, college)
                .orElseThrow(MemberNotFoundException::new);

        mail(USER_ID_IS, member.getName(), member.getUEmail(), college.getDomain(), member.getLoginId());
    }

    @Transactional
    public void findPassword(String loginId) {
        Member member = memberChecker.findByLoginId(loginId);

        String randomPassword = randomPassword();
        String hashedRandomPassword = BCrypt.hashpw(randomPassword, member.getSalt());

        mail(TEMPORARY_PASSWORD_IS, member.getName(), member.getUEmail(),
                member.getCollege().getDomain(), randomPassword);
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
            locationRepository.deleteById(member.getId());
            member.expireSessionId();
            member.changeWithdrawal(true);
            member.changeUrlCode(EMPTY_STRING);
            return;
        }
        throw new InvalidS3Exception();
    }
}
