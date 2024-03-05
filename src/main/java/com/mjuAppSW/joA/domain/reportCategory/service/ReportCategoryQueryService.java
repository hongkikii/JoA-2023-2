package com.mjuAppSW.joA.domain.reportCategory.service;

import static com.mjuAppSW.joA.common.exception.BusinessException.*;

import com.mjuAppSW.joA.domain.reportCategory.entity.ReportCategory;
import com.mjuAppSW.joA.domain.reportCategory.repository.ReportCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportCategoryQueryService {

    private final ReportCategoryRepository reportCategoryRepository;

    public ReportCategory getBy(Long reportCategoryId) {
        return reportCategoryRepository.findById(reportCategoryId)
                .orElseThrow(() -> ReportCategoryNotFoundException);
    }
}
