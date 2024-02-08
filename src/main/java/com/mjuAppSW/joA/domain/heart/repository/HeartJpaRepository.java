package com.mjuAppSW.joA.domain.heart.repository;

import com.mjuAppSW.joA.domain.heart.Heart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HeartJpaRepository extends JpaRepository<Heart, Long>  {

    @Query("SELECT COUNT(h) FROM Heart h WHERE h.member.id = :id AND DATE(h.date) = current_date")
    int countTodayHeartsById(@Param("id") Long id);

    @Query("SELECT COUNT(h) FROM Heart h WHERE h.member.id = :id")
    int countTotalHeartsById(@Param("id") Long id);

    @Query("SELECT h FROM Heart h WHERE h.giveId = :giveId AND h.member.id = :takeId AND DATE(h.date) = current_date    ")
    Optional<Heart> findTodayHeart(@Param("giveId") Long giveId, @Param("takeId") Long takeId);
}
