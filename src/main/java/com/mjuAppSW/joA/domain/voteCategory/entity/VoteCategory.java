package com.mjuAppSW.joA.domain.voteCategory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Vote_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteCategory {

    @Id
    @Column(name = "Vote_category_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    public VoteCategory(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
