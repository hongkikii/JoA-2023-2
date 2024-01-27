package com.mjuAppSW.joA.domain.member;

import com.mjuAppSW.joA.domain.college.MCollegeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Member")
public class MemberEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Member_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "Login_id", nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    @Column(name = "U_email", nullable = false)
    private String uEmail;

    @Column(length = 15, nullable = false)
    private String bio;

    @Column(name = "Url_code", nullable = false)
    private String urlCode;

    @Column(nullable = false)
    private Boolean isWithdrawal;

    @Column(name = "Session_id")
    private Long sessionId;

    @ManyToOne
    @JoinColumn(name = "College_id")
    private MCollegeEntity college;

    @Column(name = "Report_count", nullable = false)
    private Integer reportCount;

    @Column(nullable = false)
    private Integer status;

    private LocalDateTime stopStartDate;

    private LocalDateTime stopEndDate;


    public static MemberEntity fromModel(Member member) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.id = member.getId();
        memberEntity.name = member.getName();
        memberEntity.loginId = member.getLoginId();
        memberEntity.password = member.getPassword();
        memberEntity.salt = member.getSalt();
        memberEntity.uEmail = member.getUEmail();
        memberEntity.bio = member.getBio();
        memberEntity.urlCode = member.getUrlCode();
        memberEntity.isWithdrawal = member.getIsWithdrawal();
        memberEntity.sessionId = member.getSessionId();
        memberEntity.college = member.getCollege();
        memberEntity.reportCount = member.getReportCount();
        memberEntity.status = member.getStatus();
        memberEntity.stopStartDate = member.getStopStartDate();
        memberEntity.stopEndDate = member.getStopEndDate();
        return memberEntity;
    }

    public Member toModel() {
        return Member.builder()
                .id(id)
                .name(name)
                .loginId(loginId)
                .password(password)
                .salt(salt)
                .uEmail(uEmail)
                .bio(bio)
                .urlCode(urlCode)
                .isWithdrawal(isWithdrawal)
                .sessionId(sessionId)
                .college(college)
                .reportCount(reportCount)
                .status(status)
                .stopStartDate(stopStartDate)
                .stopEndDate(stopEndDate)
                .build();
    }
}
