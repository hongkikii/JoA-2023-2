package com.mjuAppSW.joA.domain.mCollege.service;

import static com.mjuAppSW.joA.common.constant.Constants.EMAIL_SPLIT;
import static com.mjuAppSW.joA.common.exception.BusinessException.*;

import com.mjuAppSW.joA.domain.mCollege.entity.MCollege;
import com.mjuAppSW.joA.domain.mCollege.repository.MCollegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MCollegeQueryService {

    private final MCollegeRepository mCollegeRepository;

    public MCollege getById(Long collegeId) {
        return mCollegeRepository.findById(collegeId)
                .orElseThrow(() -> CollegeNotFoundException);
    }

    public MCollege getByDomain(String domain) {
        return mCollegeRepository.findByDomain(EMAIL_SPLIT + domain)
                .orElseThrow(() -> CollegeNotFoundException);
    }
}
