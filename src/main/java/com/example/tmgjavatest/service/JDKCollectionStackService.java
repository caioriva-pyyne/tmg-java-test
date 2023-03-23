package com.example.tmgjavatest.service;

import com.example.tmgjavatest.exception.EmptyStackException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Class that offers basic FILO operations (push and pop).
 * It uses a ConcurrentLinkedDeque to handle FILO operations and thread-safety.
 *
 * @param <T> the type of item that the stack can store
 */
@Service
public class JDKCollectionStackService<T> implements StackService<T> {
    private final ConcurrentLinkedDeque<T> stack;

    public JDKCollectionStackService() {
        this.stack = new ConcurrentLinkedDeque<>();
    }

    @Override
    public void push(T item) {
        stack.push(item);
    }

    @Override
    public T pop() throws EmptyStackException {
        try {
            return stack.pop();
        } catch (NoSuchElementException e) {
            throw new EmptyStackException();
        }
    }
}
