package com.mjuAppSW.joA.domain.college;

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
@Table(name = "MCollege")
public class MCollegeEntity {

    @Id
    @Column(name = "College_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String domain;

    public MCollegeEntity(Long id, String name, String domain) {
        this.id = id;
        this.name = name;
        this.domain = domain;
    }
}
