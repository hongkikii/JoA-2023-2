package com.mjuAppSW.joA.geography.location.service;

import com.mjuAppSW.joA.geography.location.dto.response.NearByInfo;
import com.mjuAppSW.joA.geography.location.dto.response.NearByListResponse;
import com.mjuAppSW.joA.geography.location.entity.Location;
import com.mjuAppSW.joA.geography.location.exception.OutOfCollegeException;
import com.mjuAppSW.joA.geography.location.repository.LocationRepository;
import com.mjuAppSW.joA.domain.mCollege.entity.MCollege;
import com.mjuAppSW.joA.domain.heart.service.HeartQueryService;
import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import com.mjuAppSW.joA.geography.pCollege.entity.PCollege;
import com.mjuAppSW.joA.geography.pCollege.service.PCollegeService;
import com.mjuAppSW.joA.geography.location.dto.request.UpdateRequest;
import com.mjuAppSW.joA.geography.location.dto.response.UpdateResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final PCollegeService pCollegeService;
    private final HeartQueryService heartQueryService;
    private final LocationQueryService locationQueryService;
    private final MemberQueryService memberQueryService;

    @Transactional
    public UpdateResponse update(UpdateRequest request) {
        Member member = memberQueryService.getNormalBySessionId(request.getId());
        Long memberId = member.getId();
        Location location = locationQueryService.getBy(memberId);

        PCollege college = location.getCollege();
        double latitude = request.getLatitude();
        double longitude = request.getLongitude();
        boolean isContained = pCollegeService.isWithinCollege(latitude, longitude, college);

        Point point = getPoint(latitude, longitude, request.getAltitude());
        locationRepository.updateById(point, isContained, memberId);
        return UpdateResponse.of(isContained);
    }

    // FIXME : 분리
    private Point getPoint(double latitude, double longitude, double altitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(longitude, latitude, altitude);
        return geometryFactory.createPoint(coordinate);
    }

    public NearByListResponse getNearByList(Long sessionId, Double latitude, Double longitude, Double altitude) {
        Member member = memberQueryService.getNormalBySessionId(sessionId);
        Long memberId = member.getId();
        validateWithinCollege(locationQueryService.getBy(memberId));

        Point point = getPoint(latitude, longitude, altitude);
        MCollege mCollege = member.getCollege();
        List<Long> nearMemberIds = locationRepository.findNearIds(memberId, point, mCollege.getId());
        List<NearByInfo> nearByList = makeNearByList(member, nearMemberIds);
        return NearByListResponse.of(nearByList);
    }

    private void validateWithinCollege(Location location) {
        if (!location.getIsContained()) {
            throw new OutOfCollegeException();
        }
    }

    // FIXME : 분리
    private List<NearByInfo> makeNearByList(Member member, List<Long> nearMemberIds) {
        return nearMemberIds.stream()
                .map(nearId -> {
                    Member findMember = memberQueryService.getById(nearId);
                    boolean isLiked = heartQueryService.isTodayHeartExisted(member.getId(), nearId);
                    return NearByInfo.builder()
                            .id(findMember.getId())
                            .name(findMember.getName())
                            .urlCode(findMember.getUrlCode())
                            .bio(findMember.getBio())
                            .isLiked(isLiked)
                            .build();
                })
                .toList();
    }

    @Transactional
    public void delete(Long memberId) {
        locationRepository.deleteById(memberId);
    }
}
