package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.MemberStatus.*;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.infrastructure.ImageUploader;
import com.mjuAppSW.joA.domain.member.infrastructure.repository.MemberRepository;
import com.mjuAppSW.joA.geography.location.LocationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatusService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;
    private final ImageUploader imageUploader;

    @Scheduled(cron = "0 0 4 * * ?")
//    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void punish() {
        List<Member> joiningAll = memberRepository.findJoiningAll();
        for (Member member : joiningAll) {
            if(member.getStatus() == STEP_1_STOP_STATUS
            || member.getStatus() == STEP_2_STOP_STATUS) {

                completeStopPolicy(member);
            }
            if (member.getReportCount() >= STEP_1_REPORT_COUNT
                && member.getStatus() != STEP_1_STOP_STATUS
                && member.getStatus() != STEP_1_COMPLETE_STATUS
                && member.getStatus() != STEP_2_STOP_STATUS
                && member.getStatus() != STEP_2_COMPLETE_STATUS) {

                executeStopPolicy(member, STEP_1_REPORT_COUNT);
            }
            if (member.getReportCount() >= STEP_2_REPORT_COUNT
                && member.getStatus() != STEP_1_STOP_STATUS
                && member.getStatus() != STEP_2_STOP_STATUS
                && member.getStatus() != STEP_2_COMPLETE_STATUS) {

                executeStopPolicy(member, STEP_2_REPORT_COUNT);
            }
            if (member.getReportCount() >= STEP_3_REPORT_COUNT) {
                executeOutPolicy(member);
            }
        }
    }

    private void completeStopPolicy(Member member) {
        if(member.getStopEndDate().toLocalDate() != LocalDate.now()) {
            log.info("account stop ing : id = {}", member.getId());
            return;
        }
        if (member.getStatus() == STEP_1_STOP_STATUS) {
            memberService.updateStatus(member, STEP_1_COMPLETE_STATUS);
            log.info("account stop end : id = {}, reportCount = 5", member.getId());
        }
        if (member.getStatus() == STEP_2_STOP_STATUS) {
            memberService.updateStatus(member, STEP_2_COMPLETE_STATUS);
            log.info("account stop end : id = {}, reportCount = 10", member.getId());
        }
        memberService.updateStopStartDate(member, null);
        memberService.updateStopEndDate(member, null);
    }

    private void executeStopPolicy(Member member, int reportCount) {
        LocalDateTime today = LocalDateTime.now();
        memberService.updateStopStartDate(member, today);
        if (reportCount == STEP_1_REPORT_COUNT) {
            memberService.updateStopEndDate(member, today.plusDays(STEP_1_DATE));
            memberService.updateStatus(member, STEP_1_STOP_STATUS);
            log.info("account stop start : id = {}, reportCount = 5", member.getId());
        }
        if (reportCount == STEP_2_REPORT_COUNT) {
            memberService.updateStopEndDate(member, today.plusDays(STEP_2_DATE));
            memberService.updateStatus(member, STEP_2_STOP_STATUS);
            log.info("account stop start : id = {}, reportCount = 10", member.getId());
        }
    }

    private void executeOutPolicy(Member member) {
        memberService.updateStatus(member, STEP_3_STOP_STATUS);
        imageUploader.delete(member.getUrlCode());
        locationRepository.deleteById(member.getId());
        memberService.updateWithdrawal(member);
        log.info("account delete : id = {}, reportCount = 15", member.getId());
    }
}
