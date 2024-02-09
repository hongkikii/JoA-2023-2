package com.mjuAppSW.joA.domain.reportCategory.repository;

import com.mjuAppSW.joA.domain.reportCategory.ReportCategory;
import java.util.Optional;

public interface ReportCategoryRepository {

    Optional<ReportCategory> findById(Long reportCategoryId);
}
