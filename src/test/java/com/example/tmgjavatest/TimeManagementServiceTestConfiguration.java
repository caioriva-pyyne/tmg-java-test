package com.example.tmgjavatest;

import com.example.tmgjavatest.service.TimeManagementServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@Configuration
public class TimeManagementServiceTestConfiguration {
    @Bean
    @Primary
    public TimeManagementServiceImpl timeManagementService() {
        return mock(TimeManagementServiceImpl.class);
    }
}
