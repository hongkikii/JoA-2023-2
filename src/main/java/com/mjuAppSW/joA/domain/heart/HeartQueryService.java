package com.mjuAppSW.joA.domain.heart;

import com.mjuAppSW.joA.domain.heart.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HeartQueryService {

    private final HeartRepository heartRepository;

    public boolean isExisted(Long giveId, Long takeId) {
        return heartRepository.findTodayHeart(giveId, takeId).isPresent();
    }
}
