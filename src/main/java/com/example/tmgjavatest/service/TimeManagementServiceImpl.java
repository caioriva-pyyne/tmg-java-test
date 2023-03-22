package com.example.tmgjavatest.service;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TimeManagementServiceImpl implements TimeManagementService {
    @Override
    public Long getEpochAfterDurationInSeconds(Long seconds) {
        return Instant.now().plusSeconds(seconds).getEpochSecond();
    }

    @Override
    public Long getCurrentEpoch() {
        return Instant.now().getEpochSecond();
    }
}
