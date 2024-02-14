package com.mjuAppSW.joA.domain.location.service;

import com.mjuAppSW.joA.domain.block.exception.LocationNotFoundException;
import com.mjuAppSW.joA.domain.location.entity.Location;
import com.mjuAppSW.joA.domain.location.repository.LocationRepository;
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
                .orElseThrow(LocationNotFoundException::new);
    }
}
