package com.example.tmgjavatest.service;

import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class ConcurrentStackService implements StackService {
    private final ConcurrentLinkedDeque<String> stack = new ConcurrentLinkedDeque<>();

    @Override
    public void push(String value) {
        stack.push(value);
    }

    @Override
    public String pop() throws NoSuchElementException {
        try {
            return stack.pop();
        } catch (NoSuchElementException e) {
            return EMPTY_STACK_MSG;
        }
    }
}