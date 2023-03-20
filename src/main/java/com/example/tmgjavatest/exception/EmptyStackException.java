package com.example.tmgjavatest.exception;

public class EmptyStackException extends RuntimeException{
    public EmptyStackException() {
        super("No item could be returned because the stack is empty");
    }
}
