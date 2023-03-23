package com.example.tmgjavatest.service;

import com.example.tmgjavatest.configuration.TTLMapConfiguration;
import com.example.tmgjavatest.exception.NoKeyValuePairException;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class that offers basic Map operations (put, get and remove) with configurable time-to-live for each entry.
 * It uses the ConcurrentHashMap collection to store entries and to handle concurrency.
 *
 * @param <K> the type of the key that the map can store
 * @param <V> the type of the value that the map can store
 */
@Service
public class TTLMapServiceImpl<K, V> implements TTLMapService<K, V> {
    private final ScheduledExecutorService executor;
    private final ConcurrentMap<K, Long> ttlMap;
    private final ConcurrentMap<K, V> dataMap;
    private final TimeManagementService timeManagementService;
    private final TTLMapConfiguration configuration;

    @Autowired
    public TTLMapServiceImpl(TimeManagementService timeManagementService,
                             TTLMapConfiguration configuration) {
        this.timeManagementService = timeManagementService;
        this.configuration = configuration;
        ttlMap = new ConcurrentHashMap<>();
        dataMap = new ConcurrentHashMap<>();
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Cleaner(),
                configuration.getCleanerJobInitialDelay(),
                configuration.getCleanerJobPeriod(), TimeUnit.MILLISECONDS);
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
    public V get(K key) throws NoKeyValuePairException {
        Long expiration = ttlMap.get(key);
        if(!dataMap.containsKey(key)
                || (expiration != null && timeManagementService.getCurrentEpoch() > expiration)) {
            ttlMap.remove(key);
            dataMap.remove(key);

            throw new NoKeyValuePairException();
        }

        return dataMap.get(key);
    }

    @Override
    public void remove(K key) throws NoKeyValuePairException {
        V value = dataMap.remove(key);
        if (value == null) throw new NoKeyValuePairException();
        ttlMap.remove(key);
    }

    class Cleaner implements Runnable {
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
