package com.example.demo.project;

import com.example.demo.dto.project.ProjectGetResponse;
import com.example.demo.dto.project.ProjectPostRequest;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class ProjectGetTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private Long projectAlphaId;

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
        MvcResult alphaMvcResult = this.mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectAlphaPostRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode alphaJsonNode = objectMapper.readTree(alphaMvcResult.getResponse().getContentAsString());
        projectAlphaId = alphaJsonNode.path("id").asLong();
    }

    @Test
    @DisplayName("성공")
    void getProject() throws Exception {
        this.mockMvc.perform(get("/projects/" + projectAlphaId)
                .param("username", "admin")
                .param("password", "admin"))
                .andExpect(status().isOk())
                .andDo(document("projects/get/success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("실패 - 존재하지 않는 프로젝트 검색")
    void getUnexistProject() throws Exception {
        this.mockMvc.perform(get("/projects/" + 938474733)
                        .param("username", "admin")
                        .param("password", "admin"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실패 - 로그인 실패")
    void getProjectWithWrongAuthorization() throws Exception {
        this.mockMvc.perform(get("/projects/" + projectAlphaId)
                        .param("username", "wrongusername")
                        .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized());
    }
}
