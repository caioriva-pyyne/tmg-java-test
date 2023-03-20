package com.example.tmgjavatest.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ConcurrentStackService implements StackService {
    // If it weren't for the sake of implementing the LIFO logic it would be more efficient to usa a concurrent collection
    // such as ConcurrentLinkedDequeue. That said, this code uses a synchronized list, which is also thread-safe but
    // less efficient than concurrent collections.
    private final List<String> stack;

    public ConcurrentStackService() {
        this.stack = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void push(String value) {
        stack.add(value);
    }

    @Override
    public String pop() {
        if (stack.isEmpty()) return null;
        int lastItemIndex = stack.size() - 1;
        String item = stack.get(lastItemIndex);
        stack.remove(lastItemIndex);

        return item;
    }
}
