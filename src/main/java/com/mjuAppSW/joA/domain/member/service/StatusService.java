package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.MemberStatus.*;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.infrastructure.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
@Slf4j
public class StatusService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 4 * * ?")
//    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void check() {
        List<Member> joiningAll = memberRepository.findJoiningAll();
        for (Member member : joiningAll) {
            if(member.getStatus() == STEP_1_STOP_STATUS
            || member.getStatus() == STEP_2_STOP_STATUS) {
                finishStopPolicy(member);
            }
            if (member.getReportCount() >= STEP_3_REPORT_COUNT) {
                executeOutPolicy(member);
                break;
            }
            if (member.getReportCount() >= STEP_2_REPORT_COUNT
                && member.getStatus() != STEP_1_STOP_STATUS
                && member.getStatus() != STEP_2_STOP_STATUS
                && member.getStatus() != STEP_2_COMPLETE_STATUS) {
                executeStopPolicy(member, STEP_2_REPORT_COUNT);
                break;
            }
            if (member.getReportCount() >= STEP_1_REPORT_COUNT
                && member.getStatus() != STEP_1_STOP_STATUS
                && member.getStatus() != STEP_1_COMPLETE_STATUS
                && member.getStatus() != STEP_2_STOP_STATUS
                && member.getStatus() != STEP_2_COMPLETE_STATUS) {
                executeStopPolicy(member, STEP_1_REPORT_COUNT);
                break;
            }
        }
    }

    private void finishStopPolicy(Member member) {
        if(!member.getStopEndDate().toLocalDate().equals(LocalDate.now())) {
            log.info("account stop ing : id = {}", member.getId());
            return;
        }
        if (member.getStatus() == STEP_1_STOP_STATUS) {
            member.updateStatus(STEP_1_COMPLETE_STATUS);
            log.info("account stop end : id = {}, reportCount = 5", member.getId());
        }
        if (member.getStatus() == STEP_2_STOP_STATUS) {
            member.updateStatus(STEP_2_COMPLETE_STATUS);
            log.info("account stop end : id = {}, reportCount = 10", member.getId());
        }
        member.expireStopDate();
    }

    private void executeOutPolicy(Member member) {
        member.updateStatus(STEP_3_STOP_STATUS);
        memberService.delete(member);
        log.info("account delete : id = {}, reportCount = 15", member.getId());
    }

    private void executeStopPolicy(Member member, int reportCount) {
        LocalDateTime today = LocalDateTime.now();
        member.updateStopStartDate(today);
        if (reportCount == STEP_1_REPORT_COUNT) {
            member.updateStopEndDate(today.plusDays(STEP_1_DATE));
            member.updateStatus(STEP_1_STOP_STATUS);
            log.info("account stop start : id = {}, reportCount = 5", member.getId());
        }
        if (reportCount == STEP_2_REPORT_COUNT) {
            member.updateStopEndDate(today.plusDays(STEP_2_DATE));
            member.updateStatus(STEP_2_STOP_STATUS);
            log.info("account stop start : id = {}, reportCount = 10", member.getId());
        }
    }
}
