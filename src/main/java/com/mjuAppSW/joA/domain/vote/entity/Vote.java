package com.mjuAppSW.joA.domain.vote.entity;

import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.voteCategory.entity.VoteCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Vote_id")
    private Long id;

    @Column(name = "Give_id", nullable = false)
    private Long giveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Take_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Category_id", nullable = false)
    private VoteCategory voteCategory;

    @Column(nullable = false)
    private LocalDateTime date;

    private String hint;

    @Column(name = "Is_valid", nullable = false)
    private Boolean isValid;

    @Builder
    public Vote(Long id, Long giveId, Member member, VoteCategory voteCategory, LocalDateTime date, String hint) {
        this.id = id;
        this.giveId = giveId;
        this.member = member;
        this.voteCategory = voteCategory;
        this.date = date;
        this.hint = hint;
        this.isValid = true;
    }

    public void changeToInvalid() {
        this.isValid = false;
    }
}
