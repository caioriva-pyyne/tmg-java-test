package com.example.tmgjavatest.service;

public interface TimeManagementService {

    Long getEpochAfterDurationInSeconds(Long seconds);

    Long getCurrentEpoch();
}
