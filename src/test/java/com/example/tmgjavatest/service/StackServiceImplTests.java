package com.example.tmgjavatest.service;

import com.example.tmgjavatest.TestType;
import com.example.tmgjavatest.exception.EmptyStackException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag(TestType.UNIT_TEST)
public class StackServiceImplTests {
    protected StackService<String> stackService;

    @BeforeEach
    public void setUp() {
        stackService = getStackServiceInstance();
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

        // Assert
        assertEquals(worldValue, firstPoppedValue);
        assertEquals(againValue, secondPoppedValue);
        assertEquals(helloValue, thirdPoppedValue);
        assertThrows(EmptyStackException.class, () -> stackService.pop());
    }

    @Test
    public void classConcurrentStackService_withArbitraryItemType_shouldWorkAsExpected(){
        Object firstItem = new Object();
        Object secondItem = new Object();
        Object thirdItem = new Object();

        StackService<Object> agnosticStackService = getStackServiceInstance();
        // Act
        agnosticStackService.push(firstItem);
        agnosticStackService.push(secondItem);
        Object firstPoppedValue = agnosticStackService.pop();
        agnosticStackService.push(thirdItem);
        Object secondPoppedValue = agnosticStackService.pop();
        Object thirdPoppedValue = agnosticStackService.pop();

        // Assert
        assertEquals(secondItem, firstPoppedValue);
        assertEquals(thirdItem, secondPoppedValue);
        assertEquals(firstItem, thirdPoppedValue);
    }

    @Test
    public void pop_withEmptyStack_shouldThrowEmptyStackException() {
        // Act and assert
        assertThrows(EmptyStackException.class, () -> stackService.pop());
    }

    protected <T> StackService<T> getStackServiceInstance() {
        return new StackServiceImpl<>();
    }
}
