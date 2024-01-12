package com.mjuAppSW.joA.domain.report.vote;

import com.mjuAppSW.joA.common.auth.MemberChecker;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.report.ReportCategory;
import com.mjuAppSW.joA.domain.report.ReportCategoryRepository;
import com.mjuAppSW.joA.domain.report.vote.dto.VoteReportRequest;
import com.mjuAppSW.joA.domain.report.vote.exception.ReportCategoryNotFoundException;
import com.mjuAppSW.joA.domain.report.vote.exception.VoteNotFoundException;
import com.mjuAppSW.joA.domain.report.vote.exception.VoteReportAlreadyExistedException;
import com.mjuAppSW.joA.domain.vote.Vote;
import com.mjuAppSW.joA.domain.vote.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteReportService {
    private final VoteRepository voteRepository;
    private final VoteReportRepository voteReportRepository;
    private final ReportCategoryRepository reportCategoryRepository;
    private final MemberChecker memberChecker;

    @Transactional
    public void reportVote(VoteReportRequest request) {
        ReportCategory reportCategory = findReportCategoryById(request.getReportId());
        Long voteId = request.getVoteId();
        Vote vote = findVoteById(voteId);
        Member giveMember = memberChecker.findById(vote.getGiveId());

        checkEqualReport(voteId);

        createVoteReport(vote, reportCategory, request.getContent());
        vote.changeInvalid();
        giveMember.addReportCount();
    }

    private ReportCategory findReportCategoryById(Long id) {
        return reportCategoryRepository.findById(id)
                .orElseThrow(ReportCategoryNotFoundException::new);
    }

    private Vote findVoteById(Long id) {
        return voteRepository.findById(id)
                .orElseThrow(VoteNotFoundException::new);
    }

    private void checkEqualReport(Long voteId) {
        voteReportRepository.findByVoteId(voteId)
                .ifPresent(report -> {
                    throw new VoteReportAlreadyExistedException();});
    }

    private void createVoteReport(Vote vote, ReportCategory reportCategory, String content) {
        voteReportRepository.save(VoteReport.builder()
                                        .vote(vote)
                                        .reportCategory(reportCategory)
                                        .content(content)
                                        .build());
    }
}
