package com.mjuAppSW.joA.domain.member.infrastructure;

public interface CacheManager { //FIXME

    void add(String key, String value, int minute);

    String addRandomValue(String key, int minute);

    boolean isNotExistedKey(String key);

    boolean isExistedValue(String startWith, String checkValue);

    boolean compare(String key, String value);

    String getData(String key);

    void changeTime(String key, int minute);

    String delete(String key);
}
