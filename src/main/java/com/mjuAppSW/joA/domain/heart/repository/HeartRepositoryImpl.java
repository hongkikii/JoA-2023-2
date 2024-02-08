package com.mjuAppSW.joA.domain.heart.repository;

import com.mjuAppSW.joA.domain.heart.Heart;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HeartRepositoryImpl implements HeartRepository {

    private final HeartJpaRepository heartJpaRepository;

    @Override
    public void save(Heart heart) {
        heartJpaRepository.save(heart);
    }

    @Override
    public int countTodayHeartsById(Long id) {
        return heartJpaRepository.countTodayHeartsById(id);
    }

    @Override
    public int countTotalHeartsById(Long id) {
        return heartJpaRepository.countTotalHeartsById(id);
    }

    @Override
    public Optional<Heart> findTodayHeart(Long giveId, Long takeId) {
        return heartJpaRepository.findTodayHeart(giveId, takeId);
    }
}
