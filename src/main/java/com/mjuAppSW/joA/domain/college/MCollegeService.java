package com.mjuAppSW.joA.domain.college;

import static com.mjuAppSW.joA.common.constant.Constants.EMAIL_SPLIT;

import com.mjuAppSW.joA.domain.college.repository.MCollegeRepository;
import com.mjuAppSW.joA.geography.location.exception.CollegeNotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class MCollegeService {

    private final MCollegeRepository mCollegeRepository;

    public MCollege findById(Long collegeId) {
        return mCollegeRepository.findById(collegeId)
                .orElseThrow(CollegeNotFoundException::new);
    }

    public MCollege findByDomain(String domain) {
        return mCollegeRepository.findByDomain(EMAIL_SPLIT + domain)
                .orElseThrow(CollegeNotFoundException::new);
    }
}
