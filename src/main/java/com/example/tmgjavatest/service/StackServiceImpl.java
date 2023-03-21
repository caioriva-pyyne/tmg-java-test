package com.example.tmgjavatest.service;

import org.springframework.stereotype.Service;

@Service
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
    public synchronized T pop() {
        if (head == null) return null;

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
