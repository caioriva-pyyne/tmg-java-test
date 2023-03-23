package com.example.tmgjavatest.service;

import com.example.tmgjavatest.exception.EmptyStackException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Class that offers basic FILO operations (push and pop).
 * It uses a doubly linked list implementation with synchronized methods for
 * thread-safety.
 *
 * @param <T> the type of item that the stack can store
 */
@Service
@Primary
public class StackServiceImpl<T> implements StackService<T> {
    private Node head;
    private Node tail;

    @Override
    public synchronized void push(T item) {
        var newNode = new Node(item);
        if (head == null) {
            head = newNode;
            tail = newNode;
            return;
        }

        Node currentLastNode = tail;
        currentLastNode.next = newNode;
        newNode.previous = currentLastNode;
        tail = newNode;
    }

    @Override
    public synchronized T pop() throws EmptyStackException {
        if (head == null) throw new EmptyStackException();

        T item = tail.item;
        if (tail.previous == null) {
            head = null;
            tail = null;
            return item;
        }

        Node newLast = new Node(tail.previous);
        newLast.next = null;
        tail = newLast;
        return item;
    }

    class Node {
        private Node next;
        private Node previous;
        private final T item;

        public Node(T item) {
            this.item = item;
        }

        public Node(Node node) {
            item = node.item;
            next = node.next;
            previous = node.previous;
        }
    }
}
