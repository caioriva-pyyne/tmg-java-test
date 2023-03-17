package com.example.tmgjavatest.service;

public interface StackService {
    String EMPTY_STACK_MSG = "The stack is empty!";

    void push(String value);

     String pop();
}
