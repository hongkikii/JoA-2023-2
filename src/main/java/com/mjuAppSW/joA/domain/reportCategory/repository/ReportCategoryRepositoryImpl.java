package com.mjuAppSW.joA.domain.reportCategory.repository;

import com.mjuAppSW.joA.domain.reportCategory.entity.ReportCategory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReportCategoryRepositoryImpl implements ReportCategoryRepository {

    private final ReportCategoryJpaRepository reportCategoryJpaRepository;

    @Override
    public Optional<ReportCategory> findById(Long reportCategoryId) {
        return reportCategoryJpaRepository.findById(reportCategoryId);
    }
}
