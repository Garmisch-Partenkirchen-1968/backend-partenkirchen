package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class UserTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공하는 경우")
    void signUp() throws Exception {
        User user = User.builder().username("test-admin").password("test-admin").build();
        this.mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("공백이 있는 아이디를 만드려고 한 경우")
    void signUpWithWhitespace() throws Exception {
        User user = User.builder().username("test admin").password("test-admin").build();
        this.mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("username이 빈 경우")
    void signUpWithEmptyUsername() throws Exception {
        User user = User.builder().username("").password("test-admin").build();
        this.mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("password가 빈 경우")
    void signUpWithEmptyPassword() throws Exception {
        User user = User.builder().username("admin").password("").build();
        this.mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않은 아이디로 로그인 하려는 경우")
    void signInWithUnexistUser() throws Exception {
        User user = User.builder().username("wrongusernameasdflkjhasdflkjha").password("aadmin").build();
        this.mockMvc.perform(get("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("잘못 된 비밀번호로 로그인")
    void signInWithWrongPassword() throws Exception {
        // 테스트 계정 생성
        User userSignUp = User.builder().username("test-admin").password("test-admin").build();

        userService.signUpUser(userSignUp);
        User user = User.builder().username("test-admin").password("wrongpassword").build();
        this.mockMvc.perform(get("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 성공하는 경우")
    void signIn() throws Exception {
        // 테스트 계정 생성
        User userSignUp = User.builder().username("test-admin").password("test-admin").build();
        userService.signUpUser(userSignUp);

        // 로그인 시도
        User user = User.builder().username("test-admin").password("test-admin").build();
        this.mockMvc.perform(get("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
