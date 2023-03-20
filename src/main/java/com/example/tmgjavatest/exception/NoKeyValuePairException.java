package com.example.tmgjavatest.exception;

public class NoKeyValuePairException extends RuntimeException {
    public NoKeyValuePairException() {
        super("No value found for the specified key");
    }
}
