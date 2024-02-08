package com.mjuAppSW.joA.domain.member.service;

public interface SessionService {
    long create();

    void update();

    void checkInCache(String status, Long sessionId);
}
