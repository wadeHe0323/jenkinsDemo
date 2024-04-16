package com.wade.springsecuritydemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wade.springsecuritydemo.model.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void register() throws Exception {
        Member member = new Member();
        member.setEmail("test1@gmail.com");
        member.setPassword("111");
        member.setName("Test 1");
        member.setAge(30);

        String json = objectMapper.writeValueAsString(member);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/register")
                .header("Content-type", "application/json")
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200));

        // 測試註冊帳號是否能登入成功
        RequestBuilder loginRequestBuilder = MockMvcRequestBuilders
                .post("/userLogin")
                .with(httpBasic(member.getEmail(), member.getPassword()));

        mockMvc.perform(loginRequestBuilder)
                .andExpect(status().is(200));

        // 新註冊帳號是否可以觀看免費電影
        RequestBuilder freeRequestBuilder = MockMvcRequestBuilders
                .post("/watchFreeMovie")
                .with(httpBasic(member.getEmail(), member.getPassword()))
                .with(csrf());

        mockMvc.perform(loginRequestBuilder)
                .andExpect(status().is(200));
    }

    @Test
    public void userLogin() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/userLogin")
                .with(httpBasic("normal@gmail.com", "normal"));

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200));
    }
}
