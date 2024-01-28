package com.mjuAppSW.joA.domain.member.service;

import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.exception.SessionNotFoundException;
import com.mjuAppSW.joA.domain.member.infrastructure.repository.MemberRepository;
import com.mjuAppSW.joA.domain.member.service.port.CacheManagerImpl;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    private final MemberRepository memberRepository;
    private final CacheManagerImpl cacheManager;

    public long create() {
        long min = 1000000000L;
        long max = 9999999999L;
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
    public void update() {
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            member.updateSessionId(create());
        }
    }

    public void checkStatusInCache(String status, Long sessionId) {
        if (cacheManager.isNotExistedKey(status + sessionId)) {
            throw new SessionNotFoundException();
        }
    }
}
