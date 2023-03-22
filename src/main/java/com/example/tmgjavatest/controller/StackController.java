package com.example.tmgjavatest.controller;

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

/**
 * A Spring controller that handles stack related requests.
 */
@RestController
@RequestMapping(value = "stack", produces = "application/json")
public class StackController {
    private final StackService<String> stackService;

    /**
     * Instantiates a new StackController.
     *
     * @param stackService the stack service
     */
    @Autowired
    public StackController(StackService<String> stackService) {
        this.stackService = stackService;
    }

    /**
     * Pushes one item to the end of the stack.
     *
     * @param request that contains the item to be pushed
     */
    @PostMapping("/push")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void push(@RequestBody @Valid StackRequest request) {
        stackService.push(request.getItem());
    }

    /**
     * Pops the item in the end of the stack.
     *
     * @return a response that contains the popped item
     */
    @GetMapping(value = "/pop")
    @ResponseStatus(value = HttpStatus.OK)
    public StackResponse pop() {
        return new StackResponse(stackService.pop());
    }
}
