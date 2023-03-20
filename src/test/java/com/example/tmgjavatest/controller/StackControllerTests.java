package com.example.tmgjavatest.controller;

import com.example.tmgjavatest.TestType;
import com.example.tmgjavatest.TmgJavaTestApplication;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = TmgJavaTestApplication.class)
@AutoConfigureMockMvc
@Tag(TestType.INTEGRATION_TEST)
public class StackControllerTests {
    @Autowired
    private MockMvc mvc;

    @Test
    public void push_withoutItem_returns400() throws Exception {
        // Act and assert
        mvc.perform(post("/stack/push")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]")
                        .value("An item is required to be pushed"));
    }

    @Test
    public void pop_withFilledStack_returns200() throws Exception {
        // Arrange
        mvc.perform(post("/stack/push")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"item\":\"test\"}"))
                .andExpect(status().isNoContent());

        // Act and assert
        mvc.perform(get("/stack/pop"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("data.item").value("test"));
    }

    @Test
    public void pop_withEmptyStack_returns404() throws Exception {
        // Act and assert
        mvc.perform(get("/stack/pop")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]")
                        .value("No item could be returned because the stack is empty"));
    }
}
