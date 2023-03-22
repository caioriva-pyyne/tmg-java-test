package com.example.tmgjavatest.service;

import com.example.tmgjavatest.exception.EmptyStackException;

/**
 * Interface that offers basic FILO operations (push and pop).
 *
 * @param <T> the type of item that the stack can store
 */
public interface StackService<T> {
    /**
     * Pushes one item to the end of the stack.
     *
     * @param item the item to be pushed
     */
    void push(T item);

    /**
     * Pops the item in the end of the stack.
     *
     * @return the item
     * @throws EmptyStackException if the stack is empty
     */
    T pop() throws EmptyStackException;
}
