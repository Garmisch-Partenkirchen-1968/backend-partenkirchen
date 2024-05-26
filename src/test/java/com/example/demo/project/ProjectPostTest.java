package com.example.demo.project;

import com.example.demo.dto.project.ProjectPostRequest;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs
public class ProjectPostTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @BeforeEach
    void init() {
        // projeect를 생성할 유저 생성
        User admin = new User("admin", "admin");
        userService.signUpUser(admin);
    }

    @Test
    @DisplayName("성공")
    void postProject() throws Exception {
        ProjectPostRequest projectPostRequest = ProjectPostRequest.builder()
                .username("admin")
                .password("admin")
                .name("New Project")
                .description("This is new project.")
                .build();
        this.mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectPostRequest)))
                .andExpect(status().isCreated())
                .andDo(document("projects/post/success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("실패 - project의 name없이 생성 시도")
    void postProjectWithoutName() throws Exception {
        ProjectPostRequest projectPostRequest = ProjectPostRequest.builder()
                .username("admin")
                .password("admin")
                .description("This is new project.")
                .build();
        this.mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectPostRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실패 - project의 description없이 생성 시도")
    void postProjectWithoutDescription() throws Exception {
        ProjectPostRequest projectPostRequest = ProjectPostRequest.builder()
                .username("admin")
                .password("admin")
                .name("Wrong Project")
                .build();
        this.mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectPostRequest)))
                .andExpect(status().isBadRequest())
                .andDo(document("projects/post/fail-without-description",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("실패 - 사용자 인증 실패")
    void postProjectWithoutAuthorization() throws Exception {
        ProjectPostRequest projectPostRequest = ProjectPostRequest.builder()
                .username("ghost")
                .password("ghost")
                .name("New Project")
                .description("This is description")
                .build();
        this.mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectPostRequest)))
                .andExpect(status().isUnauthorized());
    }
}
