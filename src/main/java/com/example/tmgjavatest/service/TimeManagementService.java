package com.example.tmgjavatest.service;

/**
 * Interface that manages time-related operations.
 * It was created in order to help testing for cases that involve time check
 * without needing to wait for timed operations to occur.
 */
public interface TimeManagementService {

    /**
     * Gets the epoch of a time that is after a duration from the current time.
     *
     * @param durationInSeconds the duration in seconds
     * @return the epoch
     */
    Long getEpochAfterDurationInSeconds(Long durationInSeconds);

    /**
     * Gets the epoch of the current time.
     *
     * @return the epoch
     */
    Long getCurrentEpoch();
}
