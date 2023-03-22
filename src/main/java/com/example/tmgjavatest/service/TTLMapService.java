package com.example.tmgjavatest.service;

/**
 * Interface that offers basic Map operations (put, get and remove)
 * with configurable time-to-live for each entry.
 *
 * @param <K> the type of the key that the map can store
 * @param <V> the type of the value that the map can store
 */
public interface TTLMapService<K, V> {
    /**
     * Puts a new entry in the map.
     * If timeToLiveInSeconds is not specified the entry will not expire.
     * If the key is already being used in the map, its value will be replaced.
     *
     * @param key                 the entry key
     * @param value               the entry value
     * @param timeToLiveInSeconds the time to live in seconds
     */
    void put(K key, V value, Long timeToLiveInSeconds);

    /**
     * Gets the value based on a specified key.
     *
     * @param key the key
     * @return the value
     * @throws com.example.tmgjavatest.exception.NoKeyValuePairException if no entry is found for the specified key.
     *
     */
    V get(K key);

    /**
     * Removes an entry for the map for the specified key.
     *
     * @param key the key
     * @throws com.example.tmgjavatest.exception.NoKeyValuePairException if no entry is found for the specified key.
     */
    void remove(K key);
}
