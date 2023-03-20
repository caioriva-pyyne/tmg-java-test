package com.example.tmgjavatest.controller;

import com.example.tmgjavatest.model.dto.StackItem;
import com.example.tmgjavatest.service.StackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("stack")
public class StackController {
    private final StackService stackService;

    @Autowired
    public StackController(StackService stackService) {
        this.stackService = stackService;
    }

    @PostMapping("/push")
    @ResponseStatus(value = HttpStatus.OK)
    public void push(@RequestBody @Valid StackItem item) {
        stackService.push(item.getValue());
    }

    @GetMapping(value = "/pop", produces = "application/json")
    public String pop() {
        return stackService.pop();
    }
}
