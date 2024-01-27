package com.mjuAppSW.joA.domain.college;

import static com.mjuAppSW.joA.common.constant.Constants.EMAIL_SPLIT;

import com.mjuAppSW.joA.geography.location.exception.CollegeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MCollegeService {

    private final MCollegeRepository mCollegeRepository;

    public MCollegeEntity findById(Long collegeId) {
        return mCollegeRepository.findById(collegeId)
                .orElseThrow(CollegeNotFoundException::new);
    }

    public MCollegeEntity findByDomain(String domain) {
        return mCollegeRepository.findBydomain(EMAIL_SPLIT + domain)
                .orElseThrow(CollegeNotFoundException::new);
    }
}
