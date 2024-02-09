package com.mjuAppSW.joA.domain.heart.service;

import com.mjuAppSW.joA.domain.heart.exception.HeartAlreadyExistedException;
import com.mjuAppSW.joA.domain.heart.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HeartQueryService {

    private final HeartRepository heartRepository;

    public boolean isTodayHeartExisted(Long giveId, Long takeId) {
        return heartRepository.findTodayHeart(giveId, takeId).isPresent();
    }

    public void validateNoTodayHeart(Long giveId, Long takeId) {
        heartRepository.findTodayHeart(giveId, takeId)
                .ifPresent(heart -> {
                    throw new HeartAlreadyExistedException();});
    }
}
