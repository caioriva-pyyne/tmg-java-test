package com.example.tmgjavatest;

import com.example.tmgjavatest.configuration.TTLMapConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(TTLMapConfiguration.class)
public class TmgJavaTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TmgJavaTestApplication.class, args);
    }
}
