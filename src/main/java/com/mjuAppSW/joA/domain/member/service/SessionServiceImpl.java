package com.mjuAppSW.joA.domain.member.service;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.exception.SessionNotFoundException;
import com.mjuAppSW.joA.domain.member.infrastructure.CacheManager;
import com.mjuAppSW.joA.domain.member.infrastructure.repository.MemberRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Builder
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final MemberRepository memberRepository;
    private final CacheManager cacheManager;

    @Override
    public long create() {
        long min = 1000000000L;
        long max = 9999999999L;
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
    public void update() {
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            member.updateSessionId(create());
        }
    }

    @Override
    public void checkInCache(String status, Long sessionId) {
        if (cacheManager.isNotExistedKey(status + sessionId)) {
            throw new SessionNotFoundException();
        }
    }
}
