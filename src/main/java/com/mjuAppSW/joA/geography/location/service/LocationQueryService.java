package com.mjuAppSW.joA.geography.location.service;

import static com.mjuAppSW.joA.common.exception.BusinessException.*;

import com.mjuAppSW.joA.geography.location.entity.Location;
import com.mjuAppSW.joA.geography.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationQueryService {

    private final LocationRepository locationRepository;

    public Location getBy(Long memberId) {
        return locationRepository.findById(memberId)
                .orElseThrow(() -> LocationNotFoundException);
    }
}
