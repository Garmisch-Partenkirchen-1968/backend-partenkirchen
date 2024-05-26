package com.example.demo.project;

import com.example.demo.dto.project.ProjectPostRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs
public class ProjectsGetTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @BeforeEach
    void init() throws Exception {
        // projeect를 생성할 유저 생성
        User admin = new User("admin", "admin");
        userService.signUpUser(admin);

        // Project Alpha 생성
        ProjectPostRequest projectAlphaPostRequest = ProjectPostRequest.builder()
                .username("admin")
                .password("admin")
                .name("Project Alpha")
                .description("This is alpha.")
                .build();
        this.mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectAlphaPostRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Project beta 생성
        ProjectPostRequest projectBetaPostRequest = ProjectPostRequest.builder()
                .username("admin")
                .password("admin")
                .name("Project Beta")
                .description("This is beta.")
                .build();
        this.mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectBetaPostRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("성공")
    void getProject() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/projects")
                        .param("username", "admin")
                        .param("password", "admin"))
                .andExpect(status().isOk())
                .andDo(document("projects/gets/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .andReturn();
        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertEquals(2, jsonNode.size());
    }

    @Test
    @DisplayName("실패 - 로그인 실패")
    void getProjectWithWrongAuthorization() throws Exception {
        this.mockMvc.perform(get("/projects")
                        .param("username", "wrongusername")
                        .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized());
    }
}
