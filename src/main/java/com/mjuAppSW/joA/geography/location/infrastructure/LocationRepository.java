package com.mjuAppSW.joA.geography.location.infrastructure;

import com.mjuAppSW.joA.geography.location.Location;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Point;

public interface LocationRepository {

    Location save(Location location);

    List<Location> findAll();

    Optional<Location> findById(Long memberId);

    List<Long> findNearIds(Long memberId, Point point, Long collegeId);

    void deleteById(Long memberId);

    void deleteAll();
}
