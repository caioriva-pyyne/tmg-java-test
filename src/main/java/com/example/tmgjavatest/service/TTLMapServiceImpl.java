package com.example.tmgjavatest.service;

import com.example.tmgjavatest.exception.NoKeyValuePairException;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TTLMapServiceImpl<K, V> implements TTLMapService<K, V> {
    private static final int CLEANER_EXECUTOR_INITIAL_DELAY = 0;

    // Execution period of 900ms so the cleaner executor always runs in between expiration windows (minimal value 1s)
    private static final int CLEANER_EXECUTOR_PERIOD = 900;

    private final ScheduledExecutorService executor;
    private final ConcurrentMap<K, Long> ttlMap;
    private final ConcurrentMap<K, V> dataMap;
    private final TimeManagementService timeManagementService;

    @Autowired
    public TTLMapServiceImpl(TimeManagementService timeManagementService) {
        this.timeManagementService = timeManagementService;
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
    public void put(K key, V value, Long timeToLiveInSeconds) {
        dataMap.put(key, value);

        if (timeToLiveInSeconds != null)
            ttlMap.put(key, timeManagementService.getEpochAfterDurationInSeconds(timeToLiveInSeconds));
    }

    @Override
    public V get(K key) {
        Long expiration = ttlMap.get(key);
        if(expiration != null && timeManagementService.getCurrentEpoch() > expiration) {
            ttlMap.remove(key);
            dataMap.remove(key);
            return null;
        }

        return dataMap.get(key);
    }

    @Override
    public void remove(K key) {
        V value = dataMap.remove(key);
        if (value == null) throw new NoKeyValuePairException();
        ttlMap.remove(key);
    }

    class Cleaner implements Runnable {
        private final ConcurrentMap<K, Long> ttlMap;
        private final ConcurrentMap<K, V> dataMap;

        Cleaner(ConcurrentMap<K, Long> ttlMap, ConcurrentMap<K, V> dataMap) {
            this.ttlMap = ttlMap;
            this.dataMap = dataMap;
        }

        @Override
        public void run() {
            if (ttlMap.isEmpty()) return;

            ttlMap.entrySet()
                    .parallelStream()
                    .filter(entry -> timeManagementService.getCurrentEpoch() > entry.getValue())
                    .forEach(entry -> {
                        var key = entry.getKey();
                        ttlMap.remove(key);
                        dataMap.remove(key);
                    });
        }
    }
}
