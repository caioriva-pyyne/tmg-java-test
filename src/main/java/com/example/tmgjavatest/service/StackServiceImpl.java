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
    private Node last;

    @Override
    public synchronized void push(T item) {
        var newNode = new Node(item);
        if (head == null) {
            head = newNode;
            last = newNode;
            return;
        }

        Node currentLastNode = last;
        currentLastNode.next = newNode;
        newNode.previous = currentLastNode;
        last = newNode;
    }

    @Override
    public synchronized T pop() throws EmptyStackException {
        if (head == null) throw new EmptyStackException();

        T item = last.item;
        if (last.previous == null) {
            head = null;
            last = null;
            return item;
        }

        Node newLast = new Node(last.previous);
        newLast.next = null;
        last = newLast;
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
