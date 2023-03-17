package com.example.tmgjavatest.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StackItem {
    @NotBlank(message = "A value for an item is required to push")
    private String value;
}
