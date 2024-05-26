package com.example.demo.user;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs
public class UsersGetTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        // user1 생성
        User user1 = new User("user1", "user1");
        userService.signUpUser(user1);

        // user2 생성
        User user2 = new User("user2", "user2");
        userService.signUpUser(user2);
    }

    @Test
    @DisplayName("성공 - 모든 유저 검색")
    void getUsers() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/users")
                        .param("username", "user1")
                        .param("password", "user1"))
                .andExpect(status().isOk())
                .andDo(document("user/gets/success-get-all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .andReturn();
        User[] users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User[].class);
        assertEquals(2, users.length);
    }

    @Test
    @DisplayName("성공 - us가 포함된 user 검색")
    void getUsersWithUs() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/users")
                        .param("username", "user1")
                        .param("password", "user1")
                        .param("keyword", "us"))
                .andExpect(status().isOk())
                .andReturn();
        User[] users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User[].class);
        assertEquals(2, users.length);
    }

    @Test
    @DisplayName("성공 - 1이 포함된 user 검색")
    void getUsersWith1() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/users")
                        .param("username", "user1")
                        .param("password", "user1")
                        .param("keyword", "1"))
                .andExpect(status().isOk())
                .andDo(document("user/gets/success-search-with-1",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .andReturn();
        User[] users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User[].class);
        assertEquals(1, users.length);
    }

    @Test
    @DisplayName("성공 - no가 포함된 user 검색")
    void getUsersWithNo() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/users")
                        .param("username", "user1")
                        .param("password", "user1")
                        .param("keyword", "no"))
                .andExpect(status().isOk())
                .andReturn();
        User[] users = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User[].class);
        assertEquals(0, users.length);
    }

    @Test
    @DisplayName("실패 - 로그인 실패")
    void getUsersWithWrongPermission() throws Exception {
        this.mockMvc.perform(get("/users")
                        .param("username", "ghost")
                        .param("password", "ghost"))
                .andExpect(status().isUnauthorized());
    }
}
