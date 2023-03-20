package com.example.tmgjavatest.controller;

import com.example.tmgjavatest.exception.EmptyStackException;
import com.example.tmgjavatest.model.dto.request.StackRequest;
import com.example.tmgjavatest.model.dto.response.StackResponse;
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
@RequestMapping(value = "stack", produces = "application/json")
public class StackController {
    private final StackService<String> stackService;

    @Autowired
    public StackController(StackService<String> stackService) {
        this.stackService = stackService;
    }

    @PostMapping("/push")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void push(@RequestBody @Valid StackRequest request) {
        stackService.push(request.getItem());
    }

    @GetMapping(value = "/pop")
    @ResponseStatus(value = HttpStatus.OK)
    public StackResponse pop() {
        String item = stackService.pop();
        if(item == null) throw new EmptyStackException();

        return new StackResponse(item);
    }
}
