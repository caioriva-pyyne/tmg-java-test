package com.example.tmgjavatest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrentStackServiceTests {
    private StackService stackService;

    @BeforeEach
    public void setUp() {
        stackService = new ConcurrentStackService();
    }

    @Test
    public void stackService_inTheNormalWorkflow_shouldWorkAsExpected() {
        var helloValue = "Hello";
        var worldValue = "World";
        var againValue = "Again";

        // Act
        stackService.push(helloValue);
        stackService.push(worldValue);
        String firstPoppedValue = stackService.pop();
        stackService.push(againValue);
        String secondPoppedValue = stackService.pop();
        String thirdPoppedValue = stackService.pop();
        String fourthPoppedValue = stackService.pop();

        // Assert
        assertEquals(worldValue, firstPoppedValue);
        assertEquals(againValue, secondPoppedValue);
        assertEquals(helloValue, thirdPoppedValue);
        assertEquals(StackService.EMPTY_STACK_MSG, fourthPoppedValue);

    }

    @Test
    public void stackService_withConcurrentActions_shouldPopDuplicates()
            throws ExecutionException, InterruptedException {
        // Arrange
        var testValueA = "ut-A";
        var testValueB = "ut-B";

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        Runnable pushAValueTask = () -> stackService.push(testValueA);
        Runnable pushBValueTask = () -> stackService.push(testValueB);
        Callable<String> firstPopTask = () -> stackService.pop();
        Callable<String> secondPopTask = () -> stackService.pop();

        // Act
        executorService.submit(pushAValueTask);
        executorService.submit(pushBValueTask);
        Future<String> firstPoppedValue = executorService.submit(firstPopTask);
        Future<String> secondPoppedValue = executorService.submit(secondPopTask);

        // Assert
        assertTrue(List.of(testValueB, testValueA)
                .containsAll(List.of(firstPoppedValue.get(), secondPoppedValue.get())));
    }

    @Test
    public void pop_withEmptyStack_shouldReturnEmptyStackMessage() {
        // Act
        String firstPoppedValue = stackService.pop();
        String secondPoppedValue = stackService.pop();

        // Assert
        assertEquals(StackService.EMPTY_STACK_MSG, firstPoppedValue);
        assertEquals(StackService.EMPTY_STACK_MSG, secondPoppedValue);
    }
}
