package com.example.tmgjavatest.service;

import com.example.tmgjavatest.TestType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(TestType.UNIT_TEST)
public class StackServiceImplTests {
    private StackService<String> stackService;

    @BeforeEach
    public void setUp() {
        stackService = new StackServiceImpl<>();
    }

    @Test
    public void classConcurrentStackService_inTheNormalWorkflow_shouldWorkAsExpected() {
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
        assertEquals(null, fourthPoppedValue);

    }

    @Test
    public void classConcurrentStackService_withArbitraryItemType_shouldWorkAsExpected(){
        Object firstItem = new Object();
        Object secondItem = new Object();
        Object thirdItem = new Object();

        StackService<Object> agnosticStackService = new StackServiceImpl<>();
        // Act
        agnosticStackService.push(firstItem);
        agnosticStackService.push(secondItem);
        Object firstPoppedValue = agnosticStackService.pop();
        agnosticStackService.push(thirdItem);
        Object secondPoppedValue = agnosticStackService.pop();
        Object thirdPoppedValue = agnosticStackService.pop();
        Object fourthPoppedValue = agnosticStackService.pop();

        // Assert
        assertEquals(secondItem, firstPoppedValue);
        assertEquals(thirdItem, secondPoppedValue);
        assertEquals(firstItem, thirdPoppedValue);
        assertEquals(null, fourthPoppedValue);
    }

    @Test
    public void classConcurrentStackService_withConcurrentActions_shouldNotPopDuplicates()
            throws ExecutionException, InterruptedException {
        // Arrange
        var testValueA = "ut-A";
        var testValueB = "ut-B";
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        try {
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

        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    public void pop_withEmptyStack_shouldReturnNull() {
        // Act
        String firstPoppedValue = stackService.pop();
        String secondPoppedValue = stackService.pop();

        // Assert
        assertEquals(null, firstPoppedValue);
        assertEquals(null, secondPoppedValue);
    }
}
