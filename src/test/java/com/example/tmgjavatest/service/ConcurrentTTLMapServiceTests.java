package com.example.tmgjavatest.service;

import com.example.tmgjavatest.TestType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag(TestType.UNIT_TEST)
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
        Long timeToLiveInSeconds = 2L;
        String johnName = "John";
        String larryName = "Larry";
        String nameKey = "name";
        String ageKey = "age";

        // Act
        mapService.put(nameKey, johnName, null);
        String johnNameValue = mapService.get(nameKey);
        String emptyAgeValue = mapService.get(ageKey);
        mapService.put(nameKey, larryName, timeToLiveInSeconds);
        String larryNameValue = mapService.get(nameKey);

        // Assert
        await().atMost(Duration.ofSeconds(timeToLiveInSeconds + 1L)).until(() -> {
            String emptyNameValue = mapService.get(nameKey);

            return johnName.equals(johnNameValue) &&
                    TTLMapService.DEFAULT_VALUE.equals(emptyAgeValue) &&
                    larryName.equals(larryNameValue) &&
                    TTLMapService.DEFAULT_VALUE.equals(emptyNameValue);
        });
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
        // Arrange
        Long timeToLiveInSeconds = 2L;
        String key = "test";
        String value = "value";

        // Act
        mapService.put(key, value, timeToLiveInSeconds);

        // Assert
        await().atMost(Duration.ofSeconds(timeToLiveInSeconds + 1L)).until(() -> {
            String retrievedValue = mapService.get(key);
            return TTLMapService.DEFAULT_VALUE.equals(retrievedValue);
        });
    }

    @Test
    public void remove_withNonexistentKey_shouldNotFail() {
        // Act and assert
        assertDoesNotThrow(() -> mapService.remove("nonexistent1"));
    }
}
