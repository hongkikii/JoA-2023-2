package com.mjuAppSW.joA.domain.voteReport.repository;

import com.mjuAppSW.joA.domain.voteReport.VoteReport;
import java.util.Optional;

public interface VoteReportRepository {

    void save(VoteReport voteReport);

    Optional<VoteReport> findBy(Long voteId);
}
