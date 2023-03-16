package com.example.tmgjavatest.controller;

import com.example.tmgjavatest.TmgJavaTestApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
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
public class StackControllerTests {
    @Autowired
    private MockMvc mvc;

    @Test
    public void push_onNormalWorkflow_returns200() throws Exception {
        mvc.perform(post("/stack/push")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"value\":\"test\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void push_withEmptyItemValue_returns400() throws Exception {
        mvc.perform(post("/stack/push")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]")
                        .value("A value for an item is required to push"));
    }

    @Test
    public void pop_onNormalWorkflow_returns200() throws Exception {
        mvc.perform(post("/stack/push")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"value\":\"test\"}"))
                .andExpect(status().isOk());

        mvc.perform(get("/stack/pop"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string("test"));
    }
}
