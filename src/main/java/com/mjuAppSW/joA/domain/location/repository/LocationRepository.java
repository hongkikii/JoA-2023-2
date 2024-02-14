package com.mjuAppSW.joA.domain.location.repository;

import com.mjuAppSW.joA.domain.location.entity.Location;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Point;

public interface LocationRepository {

    Location save(Location location);

    List<Location> findAll();

    Optional<Location> findById(Long memberId);

    List<Long> findNearIds(Long memberId, Point point, Long collegeId);

    void updateById(Point point, boolean isContained, Long memberId);

    void deleteById(Long memberId);

}
