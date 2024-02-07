package com.mjuAppSW.joA.domain.member.service.port;

import com.mjuAppSW.joA.domain.member.infrastructure.CacheManager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheManagerImpl implements CacheManager { //FIXME

    private final Map<String, DataAndTime> expireDataMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void add(String key, String value, int minute) {
        DataAndTime dataAndTime = new DataAndTime(value, LocalDateTime.now());
        expireDataMap.put(key, dataAndTime);
        log.info("put to expireDataMap key = {}, value = {}, minute = {}", key, value, minute);
        long seconds = convertToSeconds(minute);
        scheduledCleanUp(key, seconds);
    }

    private long convertToSeconds(int minute) {
        return minute * 60;
    }

    @Override
    public String addRandomValue(String key, int minute) {
        String randomValue = getRandomValue();
        add(key, randomValue, minute);
        return randomValue;
    }

    private String getRandomValue() {
        int random = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(random);
    }

    @Override
    public boolean isNotExistedKey(String key) {
        return !expireDataMap.containsKey(key);
    }

    @Override
    public boolean isExistedValue(String startWith, String checkValue) {
        return expireDataMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(startWith))
                .anyMatch(entry -> entry.getValue().getData().equals(checkValue));
    }

    @Override
    public boolean compare(String key, String value) {
        DataAndTime dataAndTime = expireDataMap.get(key);
        if(dataAndTime != null) {
            if(dataAndTime.getData().equals(value))
                return true;
        }
        return false;
    }

    @Override
    public String getData(String key) {
        DataAndTime dataAndTime = expireDataMap.get(key);
        return dataAndTime.getData();
    }

    @Override
    public void changeTime(String key, int minute) {
        String savedValue = expireDataMap.get(key).getData();
        add(key, savedValue, minute);
    }

    @Override
    public String delete(String key) {
        DataAndTime remove = expireDataMap.remove(key);
        log.info("remove to expireDataMap key = {}", key);
        return remove.getData();
    }

    private void scheduledCleanUp(String key, long seconds) {
        scheduler.schedule(() -> {
            cleanUpExpired(key, seconds);
        }, seconds, TimeUnit.SECONDS);
    }

    private void cleanUpExpired(String key, long seconds) {
        DataAndTime value = expireDataMap.get(key);
        LocalDateTime savedTime = value.getSavedTime();
        if (savedTime != null && isTimeOver(savedTime, seconds)) {
            expireDataMap.remove(key);
            log.info("clean up to expireDataMap key = {}", key);
        }
    }

    private boolean isTimeOver(LocalDateTime time, long seconds) {
        return Duration.between(time, LocalDateTime.now()).getSeconds() >= seconds;
    }

    @Getter
    static class DataAndTime {
        private String data;
        private LocalDateTime savedTime;

        public DataAndTime(String data, LocalDateTime savedTime) {
            this.data = data;
            this.savedTime = savedTime;
        }
    }
}
