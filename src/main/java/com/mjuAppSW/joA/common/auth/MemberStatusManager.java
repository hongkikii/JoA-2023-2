package com.mjuAppSW.joA.common.auth;

import static com.mjuAppSW.joA.common.constant.Constants.EMPTY_STRING;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.infrastructure.MemberRepository;
import com.mjuAppSW.joA.geography.location.LocationRepository;
import com.mjuAppSW.joA.domain.member.service.port.S3Uploader;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberStatusManager {

    private final S3Uploader s3Uploader;
    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;

    @Scheduled(cron = "0 0 4 * * ?")
//    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void check() {
        List<Member> joiningAll = memberRepository.findJoiningAll();
        for (Member member : joiningAll) {
            if(member.getStatus() == 1 || member.getStatus() == 2) {
                completeStopPolicy(member);
            }
            if (member.getReportCount() >= 5 && member.getStatus() != 1 && member.getStatus() != 2
                    && member.getStatus() != 11 && member.getStatus() != 22) {
                executeStopPolicy(member, 5);
            }
            if (member.getReportCount() >= 10 && member.getStatus() != 1 && member.getStatus() != 2
                    && member.getStatus() != 22) {
                executeStopPolicy(member, 10);
            }
            if (member.getReportCount() >= 15) {
                executeOutPolicy(member);
            }
        }
    }

    private void executeStopPolicy(Member member, int reportCount) {
        LocalDateTime today = LocalDateTime.now();
        member.changeStopStartDate(today);
        if (reportCount == 5) {
            member.changeStopEndDate(today.plusDays(1));
            member.changeStatus(1);
            log.info("account stop start : id = {}, reportCount = 5", member.getId());
        }
        if (reportCount == 10) {
            member.changeStopEndDate(today.plusDays(7));
            member.changeStatus(2);
            log.info("account stop start : id = {}, reportCount = 10", member.getId());
        }
    }

    private void completeStopPolicy(Member member) {
        if(member.getStopEndDate().toLocalDate() != LocalDate.now()) {
            log.info("account stop ing : id = {}", member.getId());
            return;
        }
        if (member.getStatus() == 1) {
            member.changeStatus(11);
            log.info("account stop end : id = {}, reportCount = 5", member.getId());
        }
        if (member.getStatus() == 2) {
            member.changeStatus(22);
            log.info("account stop end : id = {}, reportCount = 10", member.getId());
        }
        member.deleteStopDate();
    }

    private void executeOutPolicy(Member member) {
        member.expireSessionId();
        member.changeWithdrawal(true);
        member.changeStatus(3);
        member.changeUrlCode(EMPTY_STRING);
        locationRepository.deleteById(member.getId());
        s3Uploader.deletePicture(member.getUrlCode());
        log.info("account delete : id = {}, reportCount = 15", member.getId());
    }
}
