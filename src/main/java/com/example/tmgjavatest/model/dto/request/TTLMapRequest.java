package com.example.tmgjavatest.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TTLMapRequest {
    @NotBlank(message = "A key for a key-value pair is required to put")
    private String key;

    @NotBlank(message = "A value for a key-value pair is required to put")
    private String value;

    private Long timeToLiveInSeconds;
}
