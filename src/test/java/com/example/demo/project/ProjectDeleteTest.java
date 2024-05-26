package com.example.demo.project;

import com.example.demo.dto.project.ProjectDeleteRequest;
import com.example.demo.dto.project.ProjectPatchRequest;
import com.example.demo.dto.project.ProjectPostRequest;
import com.example.demo.dto.user.UserSignupRequest;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.repository.ProjectRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs
public class ProjectDeleteTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private Long projectAlphaId;
    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void init() throws Exception {
        // projeect를 생성할 유저 생성
        UserSignupRequest admin = new UserSignupRequest("admin", "admin");
        userService.signUpUser(admin);

        // 외부인 생성
        UserSignupRequest foreign = new UserSignupRequest("foreign", "foreign");
        userService.signUpUser(foreign);

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
        JsonNode jsonNode = objectMapper.readTree(alphaMvcResult.getResponse().getContentAsString());
        projectAlphaId = jsonNode.path("id").asLong();
    }

    @Test
    @DisplayName("성공")
    void deleteProject() throws Exception {
        ProjectDeleteRequest projectDeleteRequest = ProjectDeleteRequest.builder()
                .username("admin")
                .password("admin")
                .build();
        this.mockMvc.perform(delete("/projects/" + projectAlphaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDeleteRequest)))
                .andExpect(status().isOk())
                .andDo(document("projects/delete/success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
        Optional<Project> optionalProjectAlpha = projectRepository.findById(projectAlphaId);
        assertTrue(optionalProjectAlpha.isEmpty());
    }

    @Test
    @DisplayName("실패 - 외부인이 삭제 시도")
    void deleteProjectWithWrongPermission() throws Exception {
        ProjectDeleteRequest projectDeleteRequest = ProjectDeleteRequest.builder()
                .username("foreign")
                .password("foreign")
                .build();
        this.mockMvc.perform(delete("/projects/" + projectAlphaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDeleteRequest)))
                .andExpect(status().isForbidden());
        Optional<Project> optionalProjectAlpha = projectRepository.findById(projectAlphaId);
        assertTrue(optionalProjectAlpha.isPresent());
    }
}
