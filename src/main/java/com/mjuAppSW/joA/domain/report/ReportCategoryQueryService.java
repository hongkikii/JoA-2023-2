package com.mjuAppSW.joA.domain.report;

import com.mjuAppSW.joA.domain.report.vote.exception.ReportCategoryNotFoundException;
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
