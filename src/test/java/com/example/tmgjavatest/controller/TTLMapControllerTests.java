package com.example.tmgjavatest.controller;

import com.example.tmgjavatest.TmgJavaTestApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = TmgJavaTestApplication.class)
@AutoConfigureMockMvc
public class TTLMapControllerTests {
    @Autowired
    private MockMvc mvc;


    @Test
    public void put_onNormalWorkflow_returns200() throws Exception {
        mvc.perform(put("/map/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"key\", \"value\":\"value\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void put_onNormalWorkflow_shouldAcceptTTLAndReturn200() throws Exception {
        mvc.perform(put("/map/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"key\", \"value\":\"value\", \"timeToLiveInSeconds\": 1}"))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource(value = "keyValueSource")
    public void put_withEmptyOrNonexistentKeyOrValue_returns400(String requestBody, String errorMessage) throws Exception {
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
        mvc.perform(put("/map/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"key\", \"value\":\"value\"}"))
                .andExpect(status().isOk());

        mvc.perform(get("/map/get?key=key"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string("value"));
    }

    @Test
    public void get_onNormalWorkflowWithExpiredKey_returns200() throws Exception {
        mvc.perform(put("/map/put")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"key\":\"key\", \"value\":\"value\", \"timeToLiveInSeconds\": 1}"))
                .andExpect(status().isOk());

        Thread.sleep(1000);

        mvc.perform(get("/map/get?key=key"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(""));
    }

    @Test
    public void get_withEmptyKey_returns400() throws Exception {
        mvc.perform(get("/map/get?key=")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("errors[0]")
                        .value("'key' request parameter should be specified and it must not be empty"));
    }

    private static Stream<Arguments> keyValueSource() {
        return Stream.of(
                arguments("{\"key\":\"\", \"value\":\"value\"}", "A key for a key-value pair is required to put"),
                arguments("{\"key\":\"key\", \"value\":\"\"}", "A value for a key-value pair is required to put"),
                arguments("{\"value\":\"value\"}", "A key for a key-value pair is required to put"),
                arguments("{\"key\":\"key\"}", "A value for a key-value pair is required to put")

        );
    }
}
