package com.mjuAppSW.joA.domain.report;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="Report_Category")
public class ReportCategory {

    @Id
    @Column(name = "Report_id")
    public Long id;

    @Column(nullable = false)
    private String name;

    public ReportCategory(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
