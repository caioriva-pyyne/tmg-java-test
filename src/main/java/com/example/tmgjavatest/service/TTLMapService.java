package com.example.tmgjavatest.service;

import java.time.Instant;

public interface TTLMapService {
    String DEFAULT_VALUE = "";

    void put(String key, String value, Long timeToLiveInSeconds);

    String get(String key);

    void remove(String key);

    record TTLValue(String value, Instant expirationTime) {}
}
