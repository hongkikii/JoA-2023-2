package com.mjuAppSW.joA.domain.college.service;

import static com.mjuAppSW.joA.common.constant.Constants.EMAIL_SPLIT;

import com.mjuAppSW.joA.domain.college.entity.MCollege;
import com.mjuAppSW.joA.domain.college.repository.MCollegeRepository;
import com.mjuAppSW.joA.geography.location.exception.CollegeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MCollegeQueryService {

    private final MCollegeRepository mCollegeRepository;

    public MCollege getById(Long collegeId) {
        return mCollegeRepository.findById(collegeId)
                .orElseThrow(CollegeNotFoundException::new);
    }

    public MCollege getByDomain(String domain) {
        return mCollegeRepository.findByDomain(EMAIL_SPLIT + domain)
                .orElseThrow(CollegeNotFoundException::new);
    }
}
