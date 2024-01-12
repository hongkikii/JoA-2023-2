package com.mjuAppSW.joA.domain.heart;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HeartRepository extends JpaRepository<Heart, Long>  {

    @Query("SELECT COUNT(h) FROM Heart h WHERE h.member.id = :id AND h.date = :today")
    int countTodayHeartsById(@Param("today") LocalDate today, @Param("id") Long id);

    @Query("SELECT COUNT(h) FROM Heart h WHERE h.member.id = :id")
    int countTotalHeartsById(@Param("id") Long id);

    @Query("SELECT h FROM Heart h WHERE h.giveId = :giveId AND h.member.id = :takeId AND h.date = :today")
    Optional<Heart> findEqualHeart(@Param("today") LocalDate today, @Param("giveId") Long giveId, @Param("takeId") Long takeId);
}
