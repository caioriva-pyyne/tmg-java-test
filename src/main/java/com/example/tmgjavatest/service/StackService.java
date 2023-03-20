package com.example.tmgjavatest.service;

public interface StackService<T> {
    void push(T item);

     T pop();
}
