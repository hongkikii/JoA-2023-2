package com.mjuAppSW.joA.geography.pCollege.service;

import com.mjuAppSW.joA.geography.pCollege.dto.PolygonRequest;
import com.mjuAppSW.joA.geography.pCollege.entity.PCollege;
import com.mjuAppSW.joA.geography.pCollege.repository.PCollegeRepository;
import com.mjuAppSW.joA.geography.location.exception.CollegeNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PCollegeService {

    private final PCollegeRepository pCollegeRepository;

    @Transactional
    public void create(PolygonRequest request) {
        PCollege college = new PCollege(request.getCollegeId(), makePolygon(request));
        pCollegeRepository.save(college);
    }

    // FIXME : 분리
    private Polygon makePolygon(PolygonRequest request) {
        GeometryFactory geometryFactory = new GeometryFactory();

        Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(request.getTopLeftLng(), request.getTopLeftLat()),
                new Coordinate(request.getTopRightLng(), request.getTopRightLat()),
                new Coordinate(request.getBottomRightLng(), request.getBottomRightLat()),
                new Coordinate(request.getBottomLeftLng(), request.getBottomLeftLat()),
                new Coordinate(request.getTopLeftLng(), request.getTopLeftLat())
        };
        return geometryFactory.createPolygon(coordinates);
    }

    public PCollege getBy(Long collegeId) {
        return pCollegeRepository.findById(collegeId)
                .orElseThrow(CollegeNotFoundException::new);
    }

    public boolean isWithinCollege(double latitude, double longitude, PCollege pCollege) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(longitude, latitude);
        Point point = geometryFactory.createPoint(coordinate);
        return pCollege.getPolygonField().contains(point);
    }

}
