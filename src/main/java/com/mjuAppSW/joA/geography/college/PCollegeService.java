package com.mjuAppSW.joA.geography.college;

import com.mjuAppSW.joA.geography.college.dto.PolygonRequest;
import com.mjuAppSW.joA.geography.college.repository.PCollegeRepository;
import com.mjuAppSW.joA.geography.location.exception.CollegeNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
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

    public PCollege findById(Long collegeId) {
        return pCollegeRepository.findById(collegeId)
                .orElseThrow(CollegeNotFoundException::new);
    }

}
