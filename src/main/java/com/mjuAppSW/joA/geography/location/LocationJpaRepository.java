package com.mjuAppSW.joA.geography.location;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationRepository extends JpaRepository<Location, Long> {


    @Query(value = "SELECT l.member_id " +
            "FROM location l " +
            "WHERE ST_DWithin(l.member_point, :point, 0.000899) " +
            "AND ABS(ST_Z(l.Member_point) - ST_Z(:point)) <= 3 " +
            "AND l.member_id <> :memberId " +
            "AND CAST(l.update_date AS DATE) = CURRENT_DATE " +
            "AND l.college_id = :collegeId " +
            "AND l.is_contained = true " +
            "AND NOT EXISTS (SELECT 1 FROM block b " +
            "                WHERE (b.blocker_id = l.member_id AND b.blocked_id = :memberId) " +
            "                   OR (b.blocker_id = :memberId AND b.blocked_id = l.member_id)) " +
            "ORDER BY ST_Distance(l.Member_point, :point) " +
            "LIMIT 50", nativeQuery = true)
    List<Long> findNearIds(@Param("memberId") Long memberId, @Param("point") Point point, @Param("collegeId") Long collegeId);

    @Override
    @Query("SELECT l FROM Location l WHERE l.id = :memberId")
    Optional<Location> findById(@Param("memberId") Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Location l WHERE l.id = :memberId ")
    void deleteById(@Param("memberId") Long memberId);

}
