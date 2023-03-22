package com.example.tmgjavatest.service;

import com.example.tmgjavatest.TestType;
import com.example.tmgjavatest.exception.NoKeyValuePairException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag(TestType.UNIT_TEST)
public class TTLMapServiceImplTests {
    private static final Long TEST_TTL = 1L;

    private TTLMapServiceImpl<String, String> mapService;

    private TimeManagementService timeManagementServiceMock;

    @BeforeEach
    public void setUp() {
        timeManagementServiceMock = mock(TimeManagementServiceImpl.class);
        when(timeManagementServiceMock.getCurrentEpoch()).thenCallRealMethod();
        when(timeManagementServiceMock.getEpochAfterDurationInSeconds(anyLong())).thenCallRealMethod();
        mapService = new TTLMapServiceImpl<>(timeManagementServiceMock);
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
        Long testExpiredEpoch = Instant.now().minusSeconds(TEST_TTL).getEpochSecond();

        when(timeManagementServiceMock.getEpochAfterDurationInSeconds(TEST_TTL)).thenReturn(testExpiredEpoch);
        when(timeManagementServiceMock.getCurrentEpoch()).thenReturn(testExpiredEpoch).thenCallRealMethod();

        // Act
        mapService.put(nameKey, johnName, null);
        String johnNameValue = mapService.get(nameKey);
        String nullAgeValue = mapService.get(ageKey);
        mapService.put(nameKey, larryName, TEST_TTL);
        String larryNameValue = mapService.get(nameKey);
        String nullNameValue = mapService.get(nameKey);

        // Assert
        assertEquals(johnName, johnNameValue);
        assertNull(nullAgeValue);
        assertEquals(larryName, larryNameValue);
        assertNull(nullNameValue);
    }

    @Test
    public void classConcurrentTTLMapService_withArbitraryKeyAndValueType_shouldWorkAsExpected() {
        //Arrange
        var value1 = new Object();
        var value2 = new Object();
        var key1 = new Object();
        var key2 = new Object();
        Long testExpiredEpoch = Instant.now().minusSeconds(TEST_TTL).getEpochSecond();

        when(timeManagementServiceMock.getEpochAfterDurationInSeconds(TEST_TTL)).thenReturn(testExpiredEpoch);
        when(timeManagementServiceMock.getCurrentEpoch()).thenReturn(testExpiredEpoch).thenCallRealMethod();

        TTLMapService<Object, Object> agnosticTTLMapService = new TTLMapServiceImpl<>(timeManagementServiceMock);

        // Act
        agnosticTTLMapService.put(key1, value1, null);
        Object retrievedValue1 = agnosticTTLMapService.get(key1);
        Object retrievedNull = agnosticTTLMapService.get(key2);
        agnosticTTLMapService.put(key1, value2, TEST_TTL);
        Object retrievedValue2 = agnosticTTLMapService.get(key1);
        Object retrievedNull2 = agnosticTTLMapService.get(key1);


        // Assert
        assertEquals(value1, retrievedValue1);
        assertNull(retrievedNull);
        assertEquals(value2, retrievedValue2);
        assertNull(retrievedNull2);
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

        when(timeManagementServiceMock.getEpochAfterDurationInSeconds(TEST_TTL))
                .thenReturn(Instant.now().minusSeconds(TEST_TTL).getEpochSecond());

        // Act
        mapService.put(key, value, TEST_TTL);

        // Assert
        assertNull(mapService.get(key));
    }

    @Test
    public void remove_withNonexistentKey_shouldThrowNoKeyValuePairException() {
        // Act and assert
        assertThrows(NoKeyValuePairException.class, () -> mapService.remove("nonexistent1"));
    }
}
