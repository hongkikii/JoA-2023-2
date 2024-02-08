package com.mjuAppSW.joA.domain.heart.repository;

import com.mjuAppSW.joA.domain.heart.Heart;
import java.util.Optional;

public interface HeartRepository {

    void save(Heart heart);

    int countTodayHeartsById(Long id);

    int countTotalHeartsById(Long id);

    Optional<Heart> findTodayHeart(Long giveId, Long takeId);
}
