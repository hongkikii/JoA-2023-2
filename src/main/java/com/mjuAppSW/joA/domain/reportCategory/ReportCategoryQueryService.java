package com.mjuAppSW.joA.domain.reportCategory;

import com.mjuAppSW.joA.domain.vote.exception.ReportCategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportCategoryQueryService {

    private final ReportCategoryRepository reportCategoryRepository;

    public ReportCategory getBy(Long reportCategoryId) {
        return reportCategoryRepository.findById(reportCategoryId)
                .orElseThrow(ReportCategoryNotFoundException::new);
    }
}
