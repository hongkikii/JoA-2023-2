package com.mjuAppSW.joA.domain.pCollege.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Polygon;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PCollege {
    @Id
    @Column(name = "College_id")
    private Long collegeId;

    @Column(name = "Polygon_field", columnDefinition = "geometry(Polygon,4326)", nullable = false)
    private Polygon polygonField;

    public PCollege(Long collegeId, Polygon polygonField) {
        this.collegeId = collegeId;
        this.polygonField = polygonField;
    }
}
