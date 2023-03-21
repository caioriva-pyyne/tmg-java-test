package com.example.tmgjavatest.service;

import com.example.tmgjavatest.TestType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Tag(TestType.UNIT_TEST)
public class TTLMapServiceImplTests {

    private static final Long TEST_TTL = 1L;
    private static final Long CLEANER_EXECUTION_MAX_WAIT_TIME = 5L;

    private TTLMapServiceImpl<String, String> mapService;

    @BeforeEach
    public void setUp() {
        mapService = new TTLMapServiceImpl<>();
    }

    @AfterEach
    public void tearDown() {
        mapService.destroy();
    }

    @Test
    public void classConcurrentTTLMapService_inTheNormalWorkflow_shouldWorkAsExpected() {
        //Arrange
        var johnName = "John";
        var larryName = "Larry";
        var nameKey = "name";
        var ageKey = "age";

        // Act
        mapService.put(nameKey, johnName, null);
        String johnNameValue = mapService.get(nameKey);
        String nullAgeValue = mapService.get(ageKey);
        mapService.put(nameKey, larryName, TEST_TTL);
        String larryNameValue = mapService.get(nameKey);

        // Assert
        await().atMost(Duration.ofSeconds(CLEANER_EXECUTION_MAX_WAIT_TIME)).until(() -> {
            String nullNameValue = mapService.get(nameKey);

            return johnName.equals(johnNameValue) &&
                    nullAgeValue == null &&
                    larryName.equals(larryNameValue) &&
                    nullNameValue == null;
        });
    }

    @Test
    public void classConcurrentTTLMapService_withArbitraryKeyAndValueType_shouldWorkAsExpected() {
        //Arrange
        var value1 = new Object();
        var value2 = new Object();
        var key1 = new Object();
        var key2 = new Object();

        TTLMapService<Object, Object> agnosticTTLMapService = new TTLMapServiceImpl<>();

        // Act
        agnosticTTLMapService.put(key1, value1, null);
        Object retrievedValue1 = agnosticTTLMapService.get(key1);
        Object retrievedNull = agnosticTTLMapService.get(key2);
        agnosticTTLMapService.put(key1, value2, TEST_TTL);
        Object retrievedValue2 = agnosticTTLMapService.get(key1);

        // Assert
        await().atMost(Duration.ofSeconds(CLEANER_EXECUTION_MAX_WAIT_TIME)).until(() -> {
            Object retrievedNull2 = agnosticTTLMapService.get(key1);

            return value1.equals(retrievedValue1) &&
                    retrievedNull == null &&
                    value2.equals(retrievedValue2) &&
                    retrievedNull2 == null;
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
    public void get_withNonexistentKey_shouldReturnNull() {
        // Act
        String firstValue = mapService.get("nonexistent1");
        String secondValue = mapService.get("nonexistent2");

        // Assert
        assertNull(firstValue);
        assertNull(secondValue);
    }

    @Test
    public void get_withExpiredKey_shouldReturnNull() {
        // Arrange
        var key = "test";
        var value = "value";

        // Act
        mapService.put(key, value, TEST_TTL);

        // Assert
        await().atMost(Duration.ofSeconds(CLEANER_EXECUTION_MAX_WAIT_TIME)).until(() -> mapService.get(key) == null);
    }

    @Test
    public void remove_withNonexistentKey_shouldNotFail() {
        // Act and assert
        assertDoesNotThrow(() -> mapService.remove("nonexistent1"));
    }
}
