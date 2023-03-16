package com.example.tmgjavatest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcurrentTTLMapServiceTests {

    private TTLMapService mapService;

    @BeforeEach
    public void setUp() {
        mapService = new ConcurrentTTLMapService();
    }

    @Test
    public void classConcurrentTTLMapService_inTheNormalWorkflow_shouldWorkAsExpected()
            throws InterruptedException {
        //Arrange
        String johnName = "John";
        String larryName = "Larry";
        String nameKey = "name";
        String ageKey = "age";

        // Act
        mapService.put(nameKey, johnName, null);
        String johnNameValue = mapService.get(nameKey);
        String emptyAgeValue = mapService.get(ageKey);
        mapService.put(nameKey, larryName, 2L);
        String larryNameValue = mapService.get(nameKey);
        Thread.sleep(2000);
        String emptyNameValue = mapService.get(nameKey);

        // Assert
        assertEquals(johnName, johnNameValue);
        assertEquals(TTLMapService.DEFAULT_VALUE, emptyAgeValue);
        assertEquals(larryName, larryNameValue);
        assertEquals(TTLMapService.DEFAULT_VALUE, emptyNameValue);
    }

    @Test
    public void put_withExistentKey_shouldReplaceIt() {
        // Act
        mapService.put("test", "value1", null);
        mapService.put("test", "value2", null);

        String value = mapService.get("test");

        // Assert
        assertEquals("value2", value);
    }

    @Test
    public void get_withNonexistentKey_shouldReturnDefaultValue() {
        // Act
        String firstValue = mapService.get("nonexistent1");
        String secondValue = mapService.get("nonexistent2");

        // Assert
        assertEquals(TTLMapService.DEFAULT_VALUE, firstValue);
        assertEquals(TTLMapService.DEFAULT_VALUE, secondValue);
    }

    @Test
    public void get_withExpiredKey_shouldReturnDefaultValue() throws InterruptedException {
        // Act
        mapService.put("test", "value", 2L);
        Thread.sleep(2000);
        String value = mapService.get("test");

        // Assert
        assertEquals(TTLMapService.DEFAULT_VALUE, value);
    }

    @Test
    public void remove_withNonexistentKey_shouldNotFail() {
        // Act and assert
        assertDoesNotThrow(() -> mapService.remove("nonexistent1"));
    }
}
