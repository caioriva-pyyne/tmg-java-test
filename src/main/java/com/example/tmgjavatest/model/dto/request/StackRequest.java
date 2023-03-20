package com.example.tmgjavatest.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StackRequest {
    @NotBlank(message = "An item is required to be pushed")
    private String item;
}
