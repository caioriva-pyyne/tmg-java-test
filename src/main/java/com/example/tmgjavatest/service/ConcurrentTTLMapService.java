package com.example.tmgjavatest.service;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ConcurrentTTLMapService implements TTLMapService {
    private static final int CLEANER_EXECUTOR_INITIAL_DELAY = 0;
    private static final int CLEANER_EXECUTOR_PERIOD = 100;

    private final ScheduledExecutorService executor;
    private final ConcurrentMap<String, Long> ttlMap;
    private final ConcurrentMap<String, String> dataMap;

    public ConcurrentTTLMapService() {
        ttlMap = new ConcurrentHashMap<>();
        dataMap = new ConcurrentHashMap<>();
        executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(new Cleaner(ttlMap, dataMap),
                CLEANER_EXECUTOR_INITIAL_DELAY, CLEANER_EXECUTOR_PERIOD, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void destroy() {
        executor.shutdownNow();
    }

    @Override
    public void put(String key, String value, Long timeToLiveInSeconds) {
        dataMap.put(key, value);

        if (timeToLiveInSeconds != null)
            ttlMap.put(key, Instant.now().plusSeconds(timeToLiveInSeconds).getEpochSecond());
    }

    @Override
    public String get(String key) {
        return dataMap.get(key);
    }

    @Override
    public void remove(String key) {
        dataMap.remove(key);
        ttlMap.remove(key);
    }

    static class Cleaner implements Runnable {
        private final ConcurrentMap<String, Long> ttlMap;
        private final ConcurrentMap<String, String> dataMap;

        Cleaner(ConcurrentMap<String, Long> ttlMap, ConcurrentMap<String, String> dataMap) {
            this.ttlMap = ttlMap;
            this.dataMap = dataMap;
        }

        @Override
        public void run() {
            if (ttlMap.isEmpty()) return;

            var now = Instant.now().getEpochSecond();
            ttlMap.entrySet()
                    .stream()
                    .filter(entry -> now > entry.getValue())
                    .forEach(entry -> {
                        var key = entry.getKey();
                        ttlMap.remove(key);
                        dataMap.remove(key);
                    });
        }
    }
}
