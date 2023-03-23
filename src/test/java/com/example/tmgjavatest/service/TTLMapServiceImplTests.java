package com.example.tmgjavatest.service;

import com.example.tmgjavatest.TestType;
import com.example.tmgjavatest.configuration.TTLMapConfiguration;
import com.example.tmgjavatest.exception.NoKeyValuePairException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        mapService = new TTLMapServiceImpl<>(timeManagementServiceMock,
                new TTLMapConfiguration(0, 20));
    }

    @AfterEach
    public void tearDown() {
        mapService.destroy();
    }

    @Test
    public void classConcurrentTTLMapService_inTheNormalWorkflow_shouldWorkAsExpected() {
        //Arrange
        var johnName = "John";
        var nameKey = "name";
        mapService.put(nameKey, johnName, null);

        // Act
        String johnNameValue = mapService.get(nameKey);

        // Assert
        assertEquals(johnName, johnNameValue);
    }

    @Test
    public void classConcurrentTTLMapService_withArbitraryKeyAndValueType_shouldWorkAsExpected() {
        //Arrange
        var value1 = new Object();
        var key1 = new Object();

        TTLMapService<Object, Object> agnosticTTLMapService = new TTLMapServiceImpl<>(timeManagementServiceMock,
                new TTLMapConfiguration(0, 20));

        // Act
        agnosticTTLMapService.put(key1, value1, null);
        Object retrieved1 = agnosticTTLMapService.get(key1);

        // Assert
        assertEquals(value1, retrieved1);
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
    public void get_withNonexistentKey_shouldThrowNoKeyValuePairException() {
        // Act and assert
        assertThrows(NoKeyValuePairException.class, () -> mapService.get("nonexistent1"));
    }

    @Test
    public void get_withExpiredKey_shouldThrowNoKeyValuePairException() {
        // Arrange
        var key = "test";
        var value = "value";

        when(timeManagementServiceMock.getEpochAfterDurationInSeconds(TEST_TTL))
                .thenReturn(Instant.now().minusSeconds(TEST_TTL).getEpochSecond());

        // Act
        mapService.put(key, value, TEST_TTL);

        // Assert
        assertThrows(NoKeyValuePairException.class, () -> mapService.get(key));
    }

    @Test
    public void remove_withNonexistentKey_shouldThrowNoKeyValuePairException() {
        // Act and assert
        assertThrows(NoKeyValuePairException.class, () -> mapService.remove("nonexistent1"));
    }
}
