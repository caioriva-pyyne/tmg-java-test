package com.example.tmgjavatest.service;

public interface TTLMapService<K, V> {
    void put(K key, V value, Long timeToLiveInSeconds);

    V get(K key);

    void remove(K key);
}
