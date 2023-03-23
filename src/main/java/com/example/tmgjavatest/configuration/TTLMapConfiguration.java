package com.example.tmgjavatest.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "map")
public class TTLMapConfiguration {
    private int cleanerJobInitialDelay;
    private int cleanerJobPeriod;
}
