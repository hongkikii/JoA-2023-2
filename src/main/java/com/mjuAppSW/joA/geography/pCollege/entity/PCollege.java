package com.mjuAppSW.joA.geography.pCollege.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Polygon;


@Entity
@Table(name = "P_college")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PCollege {
    @Id
    @Column(name = "P_college_id")
    private Long collegeId;

    @Column(name = "Polygon_field", columnDefinition = "geometry(Polygon,4326)", nullable = false)
    private Polygon polygonField;

    public PCollege(Long collegeId, Polygon polygonField) {
        this.collegeId = collegeId;
        this.polygonField = polygonField;
    }
}
