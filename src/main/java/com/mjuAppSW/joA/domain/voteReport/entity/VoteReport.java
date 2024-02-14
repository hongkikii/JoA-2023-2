package com.mjuAppSW.joA.domain.voteReport.entity;

import com.mjuAppSW.joA.domain.reportCategory.entity.ReportCategory;
import com.mjuAppSW.joA.domain.vote.entity.Vote;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Vote_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteReport {

    @Id @GeneratedValue
    @Column(name = "Report_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Vote_id", nullable = false)
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Category_id", nullable = false)
    private ReportCategory reportCategory;

    private String content;

    @Column(nullable = false)
    private LocalDateTime date;

    @Builder
    public VoteReport(Vote vote, ReportCategory reportCategory, String content, LocalDateTime date) {
        this.vote = vote;
        this.reportCategory = reportCategory;
        this.content = content;
        this.date = date;
    }
}
