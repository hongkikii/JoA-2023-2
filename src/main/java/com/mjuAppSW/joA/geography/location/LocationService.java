package com.mjuAppSW.joA.geography.location;

import com.mjuAppSW.joA.domain.heart.HeartRepository;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.service.MemberService;
import com.mjuAppSW.joA.geography.block.exception.LocationNotFoundException;
import com.mjuAppSW.joA.geography.college.PCollege;
import com.mjuAppSW.joA.geography.college.PCollegeService;
import com.mjuAppSW.joA.geography.location.dto.response.NearByInfo;
import com.mjuAppSW.joA.geography.location.dto.response.NearByListResponse;
import com.mjuAppSW.joA.geography.location.dto.request.UpdateRequest;
import com.mjuAppSW.joA.geography.location.dto.response.UpdateResponse;
import com.mjuAppSW.joA.geography.location.exception.OutOfCollegeException;
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
    private final HeartRepository heartRepository;
    private final MemberService memberService;

    @Transactional
    public void create(Member member, PCollege pCollege) {
        Location joinLocation = new Location(member.getId(), pCollege);
        locationRepository.save(joinLocation);
    }

    @Transactional
    public UpdateResponse update(UpdateRequest request) {
        Member member = memberService.getNormalBySessionId(request.getId());
        Location oldLocation = findByMemberId(member.getId());
        PCollege college = pCollegeService.findById(oldLocation.getCollege().getCollegeId());

        boolean isContained = isPointWithinCollege
                (request.getLatitude(), request.getLongitude(), college.getPolygonField());

        create(request, oldLocation, isContained);
        return UpdateResponse.of(isContained);
    }

    private Location findByMemberId(Long memberId) {
        return locationRepository.findById(memberId)
                .orElseThrow(LocationNotFoundException::new);
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
        Member member = memberService.getNormalBySessionId(sessionId);
        checkWithinCollege(findByMemberId(member.getId()));

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
                            Member findMember = memberService.getById(nearId);
                            boolean isLiked = heartRepository.findTodayHeart(member.getId(), nearId)
                                                            .isPresent();
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
    public void updateIsContained(Long memberId, boolean isContained) {
        locationRepository.findById(memberId)
                .ifPresent(location -> locationRepository.save(Location.builder().id(location.getId())
                        .college(location.getCollege())
                        .point(location.getPoint())
                        .isContained(isContained)
                        .updateDate(location.getUpdateDate()).build()));
    }

    @Transactional
    public void delete(Long memberId) {
        locationRepository.deleteById(memberId);
    }
}
