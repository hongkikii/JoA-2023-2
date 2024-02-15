package com.mjuAppSW.joA.domain.voteReport.service;

import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.domain.reportCategory.entity.ReportCategory;
import com.mjuAppSW.joA.domain.reportCategory.service.ReportCategoryQueryService;
import com.mjuAppSW.joA.domain.vote.entity.Vote;
import com.mjuAppSW.joA.domain.vote.service.VoteQueryService;
import com.mjuAppSW.joA.domain.vote.dto.VoteReportRequest;
import com.mjuAppSW.joA.domain.vote.exception.VoteReportAlreadyExistedException;
import com.mjuAppSW.joA.domain.voteReport.entity.VoteReport;
import com.mjuAppSW.joA.domain.voteReport.repository.VoteReportRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
@Slf4j
public class VoteReportService {

    private final VoteReportRepository voteReportRepository;
    private final ReportCategoryQueryService reportCategoryQueryService;
    private final VoteQueryService voteQueryService;
    private final MemberQueryService memberQueryService;

    @Transactional
    public void execute(VoteReportRequest request) {
        Long voteId = request.getVoteId();
        ReportCategory reportCategory = reportCategoryQueryService.getBy(request.getReportId());
        Vote vote = voteQueryService.getBy(voteId);
        Member giveMember = memberQueryService.getById(vote.getGiveId());

        validateNoVoteReport(voteId);
        voteReportRepository.save(VoteReport.builder()
                                    .vote(vote)
                                    .reportCategory(reportCategory)
                                    .content(request.getContent())
                                    .date(LocalDateTime.now())
                                    .build());
        vote.changeToInvalid();
        giveMember.addReportCount();
    }

    // FIXME : 현재 해당 서비스 안에서만 사용중
    public void validateNoVoteReport(Long voteId) {
        voteReportRepository.findBy(voteId)
                .ifPresent(report -> {
                    throw new VoteReportAlreadyExistedException();});
    }

}
