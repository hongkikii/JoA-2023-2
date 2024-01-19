package com.mjuAppSW.joA.geography.location;

import com.mjuAppSW.joA.common.auth.MemberChecker;
import com.mjuAppSW.joA.domain.heart.HeartRepository;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.geography.block.exception.LocationNotFoundException;
import com.mjuAppSW.joA.geography.college.PCollege;
import com.mjuAppSW.joA.geography.college.PCollegeRepository;
import com.mjuAppSW.joA.geography.location.dto.response.NearByInfo;
import com.mjuAppSW.joA.geography.location.dto.response.NearByListResponse;
import com.mjuAppSW.joA.geography.location.dto.request.UpdateRequest;
import com.mjuAppSW.joA.geography.location.dto.response.UpdateResponse;
import com.mjuAppSW.joA.geography.location.exception.CollegeNotFoundException;
import com.mjuAppSW.joA.geography.location.exception.OutOfCollegeException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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
    private final PCollegeRepository pCollegeRepository;
    private final HeartRepository heartRepository;
    private final MemberChecker memberChecker;

    @Transactional
    public UpdateResponse update(UpdateRequest request) {
        Member member = memberChecker.findBySessionId(request.getId());
        Location oldLocation = findLocation(member.getId());
        PCollege college = findCollege(oldLocation.getCollege().getCollegeId());

        memberChecker.checkStopped(member);

        boolean isContained = isPointWithinCollege
                (request.getLatitude(), request.getLongitude(), college.getPolygonField());

        createLocation(request, oldLocation, isContained);
        return UpdateResponse.of(isContained);
    }

    private Location findLocation(Long memberId) {
        return locationRepository.findById(memberId)
                .orElseThrow(LocationNotFoundException::new);
    }

    private PCollege findCollege(Long memberId) {
        return pCollegeRepository.findById(memberId)
                .orElseThrow(CollegeNotFoundException::new);
    }

    private boolean isPointWithinCollege(double latitude, double longitude, Polygon polygon) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(longitude, latitude);
        Point point = geometryFactory.createPoint(coordinate);

        return polygon.contains(point);
    }

    private void createLocation(UpdateRequest request, Location oldLocation, boolean isContained) {
        Point point = getPoint(request.getLatitude(), request.getLongitude(), request.getAltitude());
        Location newLocation = Location.builder()
                                    .id(oldLocation.getId())
                                    .college(oldLocation.getCollege())
                                    .point(point)
                                    .isContained(isContained)
                                    .updateDate(LocalDate.now())
                                    .build();
        locationRepository.save(newLocation);
    }

    private Point getPoint(double latitude, double longitude, double altitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(longitude, latitude, altitude);
        return geometryFactory.createPoint(coordinate);
    }

    public NearByListResponse getNearByList(Long sessionId, Double latitude, Double longitude, Double altitude) {
        Member member = memberChecker.findBySessionId(sessionId);
        memberChecker.checkStopped(member);
        checkWithinCollege(findLocation(member.getId()));

        Point point = getPoint(latitude, longitude, altitude);
        List<Long> nearMemberIds = locationRepository.findNearIds(member.getId(), point,
                                                                member.getCollege().getId(), LocalDate.now());
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
                            Member findMember = memberChecker.findById(nearId);
                            boolean isLiked = isEqualHeartExisted(member.getId(), nearId);
                            return NearByInfo.builder()
                                        .id(findMember.getId())
                                        .name(findMember.getName())
                                        .urlCode(findMember.getUrlCode())
                                        .bio(findMember.getBio())
                                        .isLiked(isLiked)
                                        .build();})
                        .collect(Collectors.toList());
    }

    private Boolean isEqualHeartExisted(Long giveId, Long takeId) {
        return heartRepository.findEqualHeart(LocalDate.now(), giveId, takeId)
                .isPresent();
    }
}
