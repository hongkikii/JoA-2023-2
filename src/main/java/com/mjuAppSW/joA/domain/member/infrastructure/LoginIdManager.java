package com.mjuAppSW.joA.domain.member.infrastructure;

public interface LoginIdManager {

    void validate(String id);

    void checkNotCache(Long key, String loginId);

    void checkInCache(Long key, String loginId);

    void checkInDb(String loginId);
}
