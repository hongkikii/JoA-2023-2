package com.mjuAppSW.joA.domain.voteReport.repository;

import com.mjuAppSW.joA.domain.voteReport.VoteReport;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoteReportRepositoryImpl implements VoteReportRepository {

    private final VoteReportJpaRepository voteReportJpaRepository;

    @Override
    public void save(VoteReport voteReport) {
        voteReportJpaRepository.save(voteReport);
    }

    @Override
    public Optional<VoteReport> findBy(Long voteId) {
        return voteReportJpaRepository.findByVoteId(voteId);
    }

}
