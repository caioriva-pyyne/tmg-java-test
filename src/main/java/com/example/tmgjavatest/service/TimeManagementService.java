package com.example.tmgjavatest.service;

public interface TimeManagementService {

    Long getEpochAfterDurationInSeconds(Long durationInSeconds);

    Long getCurrentEpoch();
}
