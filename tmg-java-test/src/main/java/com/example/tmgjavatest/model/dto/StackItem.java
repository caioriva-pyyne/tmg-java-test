package com.example.tmgjavatest.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString()
@NoArgsConstructor
public class StackItem {
    @NotBlank(message = "A value for an item is required to push")
    private String value;
}
