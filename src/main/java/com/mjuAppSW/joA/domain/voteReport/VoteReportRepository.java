package com.mjuAppSW.joA.domain.voteReport;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteReportRepository extends JpaRepository<VoteReport, Long> {

    @Query("SELECT vr FROM VoteReport vr WHERE vr.vote.id = :voteId")
    Optional<VoteReport> findByVoteId(@Param("voteId") Long voteId);

}
