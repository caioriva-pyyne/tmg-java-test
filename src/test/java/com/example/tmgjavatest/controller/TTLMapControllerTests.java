package com.example.tmgjavatest.controller;

import com.example.tmgjavatest.TestType;
import com.example.tmgjavatest.TimeManagementServiceTestConfiguration;
import com.example.tmgjavatest.TmgJavaTestApplication;
import com.example.tmgjavatest.service.TimeManagementService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = TmgJavaTestApplication.class)
@AutoConfigureMockMvc
@Tag(TestType.INTEGRATION_TEST)
@ContextConfiguration(classes = TimeManagementServiceTestConfiguration.class)
public class TTLMapControllerTests {
    private static final Long TEST_TTL = 1L;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TimeManagementService timeManagementService;

    @Test
    public void put_onNormalWorkflow_returns204() throws Exception {
        // Act and assert
        mvc.perform(put("/map/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"key1\", \"value\":\"value1\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void put_onNormalWorkflow_shouldAcceptTTLAndReturn204() throws Exception {
        // Act and assert
        mvc.perform(put("/map/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"key\":\"key2\", \"value\":\"value2\", \"timeToLiveInSeconds\": %s}",
                                TEST_TTL)))
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource(value = "keyValueSource")
    public void put_withEmptyOrNonexistentKeyOrValue_returns400(String requestBody, String errorMessage)
            throws Exception {
        // Act and assert
        mvc.perform(put("/map/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("errors[0]")
                        .value(errorMessage));
    }

    @Test
    public void get_onNormalWorkflow_returns200() throws Exception {
        // Arrange
        mvc.perform(put("/map/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"key3\", \"value\":\"value3\"}"))
                .andExpect(status().isNoContent());

        // Act and assert
        mvc.perform(get("/map/get?key=key3"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("data.key").value("key3"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.value").value("value3"));
    }

    @Test
    public void get_onNormalWorkflowWithExpiredKey_returns404() throws Exception {
        // Arrange
        when(timeManagementService.getEpochAfterDurationInSeconds(TEST_TTL))
                .thenReturn(Instant.now().minusSeconds(TEST_TTL).getEpochSecond());
        mvc.perform(put("/map/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"key\":\"key4\", \"value\":\"value4\", \"timeToLiveInSeconds\": %s}",
                                TEST_TTL)))
                .andExpect(status().isNoContent());

        // Act and assert
        mvc.perform(get("/map/get?key=key"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("errors[0]")
                        .value("No value found for the specified key"));
    }

    @Test
    public void get_withEmptyKey_returns400() throws Exception {
        // Act and assert
        mvc.perform(get("/map/get?key=")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("errors[0]")
                        .value("'key' request parameter should be specified and it must not be empty"));
    }

    @Test
    public void remove_withEmptyKey_returns400() throws Exception {
        // Act and assert
        mvc.perform(delete("/map/remove?key=")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("errors[0]")
                        .value("'key' request parameter should be specified and it must not be empty"));
    }

    @Test
    public void remove_withNotFoundKey_returns404() throws Exception {
        // Act and assert
        mvc.perform(delete("/map/remove?key=nonexistentKey")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("errors[0]")
                        .value("No value found for the specified key"));
    }

    private static Stream<Arguments> keyValueSource() {
        return Stream.of(
                arguments("{\"key\":\"\", \"value\":\"value5\"}", "A key for a key-value pair is required to put"),
                arguments("{\"key\":\"key6\", \"value\":\"\"}", "A value for a key-value pair is required to put"),
                arguments("{\"value\":\"value7\"}", "A key for a key-value pair is required to put"),
                arguments("{\"key\":\"key8\"}", "A value for a key-value pair is required to put")

        );
    }
}
