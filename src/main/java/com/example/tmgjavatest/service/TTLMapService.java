package com.example.tmgjavatest.service;

public interface TTLMapService {
    void put(String key, String value, Long timeToLiveInSeconds);

    String get(String key);

    void remove(String key);
}
