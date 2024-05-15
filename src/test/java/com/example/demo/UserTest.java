package com.example.demo;

import com.example.demo.dto.user.UserUpdatePasswordRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Test
    @DisplayName("없는 계정의 비밀번호를 바꾸려 한 경우")
    void updatePasswordWithNotExistUser() throws Exception {
        // 내 계정 생성
        User userSignUp = User.builder().username("test").password("test").build();
        userService.signUpUser(userSignUp);

        User user = User.builder().username("test").password("test").build();
        this.mockMvc.perform(patch("/user/98273")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("다른 사람의 비밀번호를 바꾸려 한 경우")
    void updatePasswordAnotherUser() throws Exception {
        // 내 계정 생성
        User userSignUp = User.builder().username("test").password("test").build();
        userService.signUpUser(userSignUp);

        // 다른 사람 계정 생성
        User anotherUserSignUp = User.builder().username("another").password("another").build();
        anotherUserSignUp = userService.signUpUser(anotherUserSignUp);

        User user = User.builder().username("test").password("test").build();
        this.mockMvc.perform(patch("/user/" + anotherUserSignUp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("비밀번호를 바꾸려는데 기존 비밀번호가 틀린 경우")
    void updatePasswordWithWrongPassword() throws Exception {
        // 내 계정 생성
        User userSignUp = User.builder().username("test").password("test").build();
        userSignUp = userService.signUpUser(userSignUp);

        User user = User.builder().username("test").password("wrongpassword").build();
        this.mockMvc.perform(patch("/user/" + userSignUp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("새로운 비밀번호없이 patch하는 경우")
    void updatePasswordWithoutPassword() throws Exception {
        // 내 계정 생성
        User userSignUp = User.builder().username("test").password("test").build();
        userSignUp = userService.signUpUser(userSignUp);

        // 비밀번호 변경 시도
        UserUpdatePasswordRequest user = UserUpdatePasswordRequest.builder()
                .username("test")
                .password("test")
                .build();
        this.mockMvc.perform(patch("/user/" + userSignUp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("새로운 비밀번호가 empty string인 경우")
    void updatePasswordWithEmptyPassword() throws Exception {
        // 내 계정 생성
        User userSignUp = User.builder().username("test").password("test").build();
        userSignUp = userService.signUpUser(userSignUp);

        // 비밀번호 변경 시도
        UserUpdatePasswordRequest user = UserUpdatePasswordRequest.builder()
                .username("test")
                .password("test")
                .newPassword("")
                .build();
        this.mockMvc.perform(patch("/user/" + userSignUp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("성공적으로 비밀번호를 바꾸는 경우")
    void updatePassword() throws Exception {
        // 내 계정 생성
        User userSignUp = User.builder().username("test").password("test").build();
        userSignUp = userService.signUpUser(userSignUp);

        // 비밀번호 변경 시도
        UserUpdatePasswordRequest user = UserUpdatePasswordRequest.builder()
                .username("test")
                .password("test")
                .newPassword("newpassword")
                .build();
        this.mockMvc.perform(patch("/user/" + userSignUp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("없는 계정을 삭제하려고 하는 경우")
    void deleteNotExistUser() throws Exception {
        // 내 계정 생성
        User userSignUp = User.builder().username("test").password("test").build();
        userService.signUpUser(userSignUp);

        User user = User.builder().username("test").password("test").build();
        this.mockMvc.perform(delete("/user/3484539")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("다른 사람 계정을 삭제하려고 하는 경우")
    void deleteAnotherUser() throws Exception {
        // 내 계정 생성
        User userSignUp = User.builder().username("test").password("test").build();
        userService.signUpUser(userSignUp);

        // 다른 사람 계정 생성
        User anotherUserSignUp = User.builder().username("another").password("another").build();
        anotherUserSignUp = userService.signUpUser(anotherUserSignUp);

        // 삭제 시도
        User user = User.builder().username("test").password("test").build();
        this.mockMvc.perform(delete("/user/" + anotherUserSignUp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("자기 계정을 삭제하려는데, 비밀번호가 틀린 경우")
    void deleteMeWithWrongPassword() throws Exception {
        // 내 계정 생성
        User userSignUp = User.builder().username("test").password("test").build();
        userSignUp = userService.signUpUser(userSignUp);

        // 삭제 시도
        User user = User.builder().username("test").password("wrongpassword").build();
        this.mockMvc.perform(delete("/user/" + userSignUp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("계정을 성공적으로 삭제한 경우")
    void deleteMe() throws Exception {
        // 내 계정 생성
        User userSignUp = User.builder().username("test").password("test").build();
        userSignUp = userService.signUpUser(userSignUp);

        // 삭제 시도
        User user = User.builder().username("test").password("test").build();
        this.mockMvc.perform(delete("/user/" + userSignUp.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }
}
