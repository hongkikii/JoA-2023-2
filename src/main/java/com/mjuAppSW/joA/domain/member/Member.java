package com.mjuAppSW.joA.domain.member;

import static com.mjuAppSW.joA.common.constant.Constants.EMPTY_STRING;
import static com.mjuAppSW.joA.common.constant.Constants.ZERO;

import com.mjuAppSW.joA.domain.college.MCollegeEntity;
import com.mjuAppSW.joA.domain.member.dto.request.JoinRequest;
import com.mjuAppSW.joA.domain.member.infrastructure.PasswordManager;
import com.mjuAppSW.joA.domain.member.service.port.PasswordManagerImpl;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Member {

    private final Long id;
    private final String name;
    private final String loginId;
    private final String password;
    private final String salt;
    private final String uEmail;
    private final String bio;
    private final String urlCode;
    private final Boolean isWithdrawal;
    private final Long sessionId;
    private final MCollegeEntity college;
    private final Integer reportCount;
    private final Integer status;
    private final LocalDateTime stopStartDate;
    private final LocalDateTime stopEndDate;

    @Builder
    public Member(Long id, String name, String loginId, String password, String salt,
                  String uEmail, String bio, String urlCode, Boolean isWithdrawal,
                  Long sessionId, MCollegeEntity college, Integer reportCount,
                  Integer status, LocalDateTime stopStartDate, LocalDateTime stopEndDate) {

        this.id = id;
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.salt = salt;
        this.uEmail = uEmail;
        this.bio = bio;
        this.urlCode = urlCode;
        this.isWithdrawal = isWithdrawal;
        this.sessionId = sessionId;
        this.college = college;
        this.reportCount = reportCount;
        this.status = status;
        this.stopStartDate = stopStartDate;
        this.stopEndDate = stopEndDate;
    }

    public static Member create(JoinRequest request, String uEmail, MCollegeEntity mCollegeEntity) {

        PasswordManager passwordManager = new PasswordManagerImpl();
        String salt = passwordManager.createSalt();
        String hashedPassword = passwordManager.createHashed(request.getPassword(), salt);

        return Member.builder()
                .name(request.getName())
                .loginId(request.getLoginId())
                .password(hashedPassword)
                .salt(salt)
                .uEmail(uEmail)
                .bio(EMPTY_STRING)
                .urlCode(EMPTY_STRING)
                .isWithdrawal(false)
                .sessionId(request.getId())
                .college(mCollegeEntity)
                .reportCount(ZERO)
                .status(ZERO)
                .stopStartDate(null)
                .stopEndDate(null)
                .build();
    }

    public static Member withdrawal(Member member) {
        return Member.builder()
                .id(member.getId())
                .name(member.getName())
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .salt(member.getSalt())
                .uEmail(member.getUEmail())
                .bio(member.getBio())
                .urlCode(EMPTY_STRING)
                .isWithdrawal(true)
                .sessionId(null)
                .college(member.getCollege())
                .reportCount(member.getReportCount())
                .status(member.getStatus())
                .stopStartDate(member.getStopStartDate())
                .stopEndDate(member.getStopEndDate())
                .build();
    }

    public static Member updateUrlCode(Member member, String urlCode) {
        return Member.builder()
                .id(member.getId())
                .name(member.getName())
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .salt(member.getSalt())
                .uEmail(member.getUEmail())
                .bio(member.getBio())
                .urlCode(urlCode)
                .isWithdrawal(member.getIsWithdrawal())
                .sessionId(member.getSessionId())
                .college(member.getCollege())
                .reportCount(member.getReportCount())
                .status(member.getStatus())
                .stopStartDate(member.getStopStartDate())
                .stopEndDate(member.getStopEndDate())
                .build();
    }

    public static Member updateBio(Member member, String bio) {
        return Member.builder()
                .id(member.getId())
                .name(member.getName())
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .salt(member.getSalt())
                .uEmail(member.getUEmail())
                .bio(bio)
                .urlCode(member.getUrlCode())
                .isWithdrawal(member.getIsWithdrawal())
                .sessionId(member.getSessionId())
                .college(member.getCollege())
                .reportCount(member.getReportCount())
                .status(member.getStatus())
                .stopStartDate(member.getStopStartDate())
                .stopEndDate(member.getStopEndDate())
                .build();
    }

    public static Member updatePassword(Member member, String password) {
        return Member.builder()
                .id(member.getId())
                .name(member.getName())
                .loginId(member.getLoginId())
                .password(password)
                .salt(member.getSalt())
                .uEmail(member.getUEmail())
                .bio(member.getBio())
                .urlCode(member.getUrlCode())
                .isWithdrawal(member.getIsWithdrawal())
                .sessionId(member.getSessionId())
                .college(member.getCollege())
                .reportCount(member.getReportCount())
                .status(member.getStatus())
                .stopStartDate(member.getStopStartDate())
                .stopEndDate(member.getStopEndDate())
                .build();
    }

    public static Member updateSessionId(Member member, Long sessionId) {
        return Member.builder()
                .id(member.getId())
                .name(member.getName())
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .salt(member.getSalt())
                .uEmail(member.getUEmail())
                .bio(member.getBio())
                .urlCode(member.getUrlCode())
                .isWithdrawal(member.getIsWithdrawal())
                .sessionId(sessionId)
                .college(member.getCollege())
                .reportCount(member.getReportCount())
                .status(member.getStatus())
                .stopStartDate(member.getStopStartDate())
                .stopEndDate(member.getStopEndDate())
                .build();
    }

    public static Member updateStopStartDate(Member member, LocalDateTime stopStartDate) {
        return Member.builder()
                .id(member.getId())
                .name(member.getName())
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .salt(member.getSalt())
                .uEmail(member.getUEmail())
                .bio(member.getBio())
                .urlCode(member.getUrlCode())
                .isWithdrawal(member.getIsWithdrawal())
                .sessionId(member.getSessionId())
                .college(member.getCollege())
                .reportCount(member.getReportCount())
                .status(member.getStatus())
                .stopStartDate(stopStartDate)
                .stopEndDate(member.getStopEndDate())
                .build();
    }

    public static Member updateStopEndDate(Member member, LocalDateTime stopEndDate) {
        return Member.builder()
                .id(member.getId())
                .name(member.getName())
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .salt(member.getSalt())
                .uEmail(member.getUEmail())
                .bio(member.getBio())
                .urlCode(member.getUrlCode())
                .isWithdrawal(member.getIsWithdrawal())
                .sessionId(member.getSessionId())
                .college(member.getCollege())
                .reportCount(member.getReportCount())
                .status(member.getStatus())
                .stopStartDate(member.getStopStartDate())
                .stopEndDate(stopEndDate)
                .build();
    }

    public static Member updateStatus(Member member, int status) {
        return Member.builder()
                .id(member.getId())
                .name(member.getName())
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .salt(member.getSalt())
                .uEmail(member.getUEmail())
                .bio(member.getBio())
                .urlCode(member.getUrlCode())
                .isWithdrawal(member.getIsWithdrawal())
                .sessionId(member.getSessionId())
                .college(member.getCollege())
                .reportCount(member.getReportCount())
                .status(status)
                .stopStartDate(member.getStopStartDate())
                .stopEndDate(member.getStopEndDate())
                .build();
    }

    public static Member updateReportCount(Member member, int add) {
        return Member.builder()
                .id(member.getId())
                .name(member.getName())
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .salt(member.getSalt())
                .uEmail(member.getUEmail())
                .bio(member.getBio())
                .urlCode(member.getUrlCode())
                .isWithdrawal(member.getIsWithdrawal())
                .sessionId(member.getSessionId())
                .college(member.getCollege())
                .reportCount(member.getReportCount() + add)
                .status(member.getStatus())
                .stopStartDate(member.getStopStartDate())
                .stopEndDate(member.getStopEndDate())
                .build();
    }
}
