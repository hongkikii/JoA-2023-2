package com.mjuAppSW.joA.domain.member.service;

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
        memberService.updateStopStartDate(member, today);
        if (reportCount == 5) {
            memberService.updateStopEndDate(member, today.plusDays(1));
            memberService.updateStatus(member, 1);
            log.info("account stop start : id = {}, reportCount = 5", member.getId());
        }
        if (reportCount == 10) {
            memberService.updateStopEndDate(member, today.plusDays(7));
            memberService.updateStatus(member, 1);
            log.info("account stop start : id = {}, reportCount = 10", member.getId());
        }
    }

    private void completeStopPolicy(Member member) {
        if(member.getStopEndDate().toLocalDate() != LocalDate.now()) {
            log.info("account stop ing : id = {}", member.getId());
            return;
        }
        if (member.getStatus() == 1) {
            memberService.updateStatus(member, 11);
            log.info("account stop end : id = {}, reportCount = 5", member.getId());
        }
        if (member.getStatus() == 2) {
            memberService.updateStatus(member, 22);
            log.info("account stop end : id = {}, reportCount = 10", member.getId());
        }
        memberService.updateStopStartDate(member, null);
        memberService.updateStopEndDate(member, null);
    }

    private void executeOutPolicy(Member member) {
        memberService.updateStatus(member, 3);
        imageUploader.delete(member.getUrlCode());
        locationRepository.deleteById(member.getId());
        memberService.updateWithdrawal(member);
        log.info("account delete : id = {}, reportCount = 15", member.getId());
    }
}
