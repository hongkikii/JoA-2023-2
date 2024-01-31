package com.mjuAppSW.joA.geography.location.infrastructure;

import com.mjuAppSW.joA.geography.location.Location;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LocationRepositoryImpl implements LocationRepository{

    private final LocationJpaRepository locationJpaRepository;


    @Override
    public Location save(Location location) {
        return locationJpaRepository.save(location);
    }

    @Override
    public Optional<Location> findById(Long memberId) {
        return locationJpaRepository.findById(memberId);
    }

    @Override
    public List<Long> findNearIds(Long memberId, Point point, Long collegeId) {
        return locationJpaRepository.findNearIds(memberId, point, collegeId);
    }

    @Override
    public void deleteById(Long memberId) {
        locationJpaRepository.deleteById(memberId);
    }
}