package com.example.tmgjavatest.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConcurrentTTLMapService implements TTLMapService {
    private final Map<String, TTLValue> map = new ConcurrentHashMap<>();

    @Override
    public void put(String key, String value, Long timeToLiveInSeconds) {
        map.put(key, new TTLValue(value, timeToLiveInSeconds != null ?
                Instant.now().plusSeconds(timeToLiveInSeconds) : null));
    }

    @Override
    public String get(String key) {
        TTLValue ttlValue = map.computeIfPresent(key, (k, v) ->
                v.expirationTime() != null ? (Instant.now().isAfter(v.expirationTime()) ? null : v) : v);

        return ttlValue != null ? ttlValue.value() : DEFAULT_VALUE;
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }
}
