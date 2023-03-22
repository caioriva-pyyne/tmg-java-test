package com.example.tmgjavatest.service;

import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class JDKCollectionStackService<T> implements StackService<T> {
    private final ConcurrentLinkedDeque<T> stack;

    public JDKCollectionStackService() {
        this.stack = new ConcurrentLinkedDeque<>();
    }

    @Override
    public void push(T item) {
        stack.add(item);
    }

    @Override
    public T pop() {
        try {
            return stack.removeLast();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
