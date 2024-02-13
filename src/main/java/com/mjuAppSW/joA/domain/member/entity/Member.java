package com.mjuAppSW.joA.domain.member.entity;

import static com.mjuAppSW.joA.common.constant.Constants.EMPTY_STRING;
import static com.mjuAppSW.joA.common.constant.Constants.ZERO;

import com.mjuAppSW.joA.domain.college.entity.MCollege;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

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
    private MCollege college;

    @Column(name = "Report_count", nullable = false)
    private Integer reportCount;

    @Column(nullable = false)
    private Integer status;

    private LocalDateTime stopStartDate;

    private LocalDateTime stopEndDate;

    @Builder
    public Member(Long id, String name, String loginId, String password, String salt, String uEmail, MCollege college, Long sessionId) {
        this.id = id;
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.salt = salt;
        this.uEmail = uEmail;
        this.college = college;
        this.isWithdrawal = false;
        this.bio = EMPTY_STRING;
        this.urlCode = EMPTY_STRING;
        this.sessionId = sessionId;
        this.reportCount = ZERO;
        this.status = ZERO;
    }

    public void updateSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public void expireSessionId() {
        this.sessionId = null;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }

    public void deleteBio() {
        this.bio = EMPTY_STRING;
    }

    public void updateUrlCode(String urlCode) {
        this.urlCode = urlCode;
    }

    public void deleteUrlCode() {
        this.urlCode = EMPTY_STRING;
    }

    public void addReportCount() {
        this.reportCount++;
    }

    public void updateStatus(int status) {
        this.status = status;
    }

    public void updateStopStartDate(LocalDateTime stopStartDate) {
        this.stopStartDate = stopStartDate;
    }

    public void updateStopEndDate(LocalDateTime stopEndDate) {
        this.stopEndDate = stopEndDate;
    }

    public void expireStopDate() {
        this.stopStartDate = null;
        this.stopEndDate = null;
    }

    public void setWithdrawal() {
        expireSessionId();
        deleteUrlCode();
        this.isWithdrawal = true;
    }
}