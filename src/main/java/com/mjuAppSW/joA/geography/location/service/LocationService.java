package com.mjuAppSW.joA.geography.location.service;

import com.mjuAppSW.joA.domain.heart.service.HeartQueryService;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.geography.college.PCollege;
import com.mjuAppSW.joA.geography.college.PCollegeService;
import com.mjuAppSW.joA.geography.location.Location;
import com.mjuAppSW.joA.geography.location.dto.response.NearByInfo;
import com.mjuAppSW.joA.geography.location.dto.response.NearByListResponse;
import com.mjuAppSW.joA.geography.location.dto.request.UpdateRequest;
import com.mjuAppSW.joA.geography.location.dto.response.UpdateResponse;
import com.mjuAppSW.joA.geography.location.exception.OutOfCollegeException;
import com.mjuAppSW.joA.geography.location.repository.LocationRepository;
import com.mjuAppSW.joA.geography.location.service.LocationQueryService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;
    private final PCollegeService pCollegeService;
    private final HeartQueryService heartQueryService;
    private final LocationQueryService locationQueryService;
    private final MemberQueryService memberQueryService;

    @Transactional
    public UpdateResponse update(UpdateRequest request) {
        Member member = memberQueryService.getNormalBySessionId(request.getId());
        Location oldLocation = locationQueryService.getBy(member.getId());
        PCollege college = pCollegeService.getBy(oldLocation.getCollege().getCollegeId());

        boolean isContained = isPointWithinCollege
                (request.getLatitude(), request.getLongitude(), college.getPolygonField());

        create(request, oldLocation, isContained);
        return UpdateResponse.of(isContained);
    }

    private boolean isPointWithinCollege(double latitude, double longitude, Polygon polygon) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(longitude, latitude);
        Point point = geometryFactory.createPoint(coordinate);

        return polygon.contains(point);
    }

    private void create(UpdateRequest request, Location oldLocation, boolean isContained) {
        Point point = getPoint(request.getLatitude(), request.getLongitude(), request.getAltitude());
        Location newLocation = Location.builder()
                                    .id(oldLocation.getId())
                                    .college(oldLocation.getCollege())
                                    .point(point)
                                    .isContained(isContained)
                                    .updateDate(LocalDateTime.now())
                                    .build();
        locationRepository.save(newLocation);
    }

    private Point getPoint(double latitude, double longitude, double altitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(longitude, latitude, altitude);
        return geometryFactory.createPoint(coordinate);
    }

    public NearByListResponse getNearByList
            (Long sessionId, Double latitude, Double longitude, Double altitude) {
        Member member = memberQueryService.getNormalBySessionId(sessionId);
        checkWithinCollege(locationQueryService.getBy(member.getId()));

        Point point = getPoint(latitude, longitude, altitude);
        System.out.println(member.getId());
        List<Long> nearMemberIds = locationRepository.findNearIds
                (member.getId(), point, member.getCollege().getId());
        System.out.println(nearMemberIds.size());
        List<NearByInfo> nearByList = makeNearByList(member, nearMemberIds);
        return NearByListResponse.of(nearByList);
    }

    private void checkWithinCollege(Location location) {
        if (!location.getIsContained()) {
            throw new OutOfCollegeException();
        }
    }

    private List<NearByInfo> makeNearByList(Member member, List<Long> nearMemberIds) {
        return nearMemberIds.stream()
                        .map(nearId -> {
                            Member findMember = memberQueryService.getById(nearId);
                            boolean isLiked = heartQueryService.isExisted(member.getId(), nearId);
                            return NearByInfo.builder()
                                        .id(findMember.getId())
                                        .name(findMember.getName())
                                        .urlCode(findMember.getUrlCode())
                                        .bio(findMember.getBio())
                                        .isLiked(isLiked)
                                        .build();})
                        .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long memberId) {
        locationRepository.deleteById(memberId);
    }
}
