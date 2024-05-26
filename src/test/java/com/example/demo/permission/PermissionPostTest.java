package com.example.demo.permission;

import com.example.demo.dto.Permission.PermissionPostRequest;
import com.example.demo.dto.project.ProjectPostRequest;
import com.example.demo.dto.user.UserSignupRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PermissionService;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
public class PermissionPostTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    Long project1Id;
    Long project2Id;

    Long admin1Id;
    Long admin2Id;
    Long admin3Id;
    Long PLId;
    Long testerId;
    Long devId;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // project 1의 admin 1
        UserSignupRequest admin1 = UserSignupRequest.builder()
                .username("admin1")
                .password("admin1")
                .build();
        admin1Id = userService.signUpUser(admin1).getId();

        // project 1의 admin 2
        UserSignupRequest admin2 = UserSignupRequest.builder()
                .username("admin2")
                .password("admin2")
                .build();
        admin2Id = userService.signUpUser(admin2).getId();

        // project 2의 admin 3
        UserSignupRequest admin3 = UserSignupRequest.builder()
                .username("admin3")
                .password("admin3")
                .build();
        admin3Id = userService.signUpUser(admin3).getId();

        // PL
        UserSignupRequest PL = UserSignupRequest.builder()
                .username("PL")
                .password("PL")
                .build();
        PLId = userService.signUpUser(PL).getId();

        // tester
        UserSignupRequest tester = UserSignupRequest.builder()
                .username("tester")
                .password("tester")
                .build();
        testerId = userService.signUpUser(tester).getId();

        // dev
        UserSignupRequest dev = UserSignupRequest.builder()
                .username("dev")
                .password("dev")
                .build();
        devId = userService.signUpUser(dev).getId();

        // project 1 생성
        ProjectPostRequest project1PostRequest = ProjectPostRequest.builder()
                .username("admin1")
                .password("admin2")
                .name("project 1")
                .description("this is project 1")
                .build();
        project1Id = projectService.createProject(project1PostRequest).getId();

        // project 2 생성
        ProjectPostRequest project2PostRequest = ProjectPostRequest.builder()
                .username("admin3")
                .password("admin3")
                .name("project 2")
                .description("this is project 2")
                .build();
        project2Id = projectService.createProject(project2PostRequest).getId();

        // project 1에 admin2를 admin으로 할당
        PermissionPostRequest permissionPostRequest = PermissionPostRequest.builder()
                .username("admin1")
                .password("admin1")
                .permissions(new boolean[] {true,false,false,false})
                .build();
        permissionService.addPermission(project1Id, admin2Id, permissionPostRequest);

        // project 2에 dev를 dev로 할당
        PermissionPostRequest permissionPostRequest2 = PermissionPostRequest.builder()
                .username("admin3")
                .password("admin3")
                .permissions(new boolean[] {false, false, true, false})
                .build();
        permissionService.addPermission(project2Id, devId, permissionPostRequest2);
    }

    @Test
    @DisplayName("Admin1이 Project 1에 Tester 할당 성공")
    void TesterPermission() throws Exception {
        // project 1에 Tester를 할당
        PermissionPostRequest permissionPostRequest = PermissionPostRequest.builder()
                .username("admin1")
                .password("admin1")
                .permissions(new boolean[] {false, false, true, false})
                .build();

        mockMvc.perform(post("/projects/" + project1Id + "/permissions/" + testerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(permissionPostRequest)))
                .andExpect(status().isOk());

        assertEquals(projectService.getProject(project1Id).getMembers().get(userRepository.findById(testerId).get()), (1 << 1));
    }

    @Test
    @DisplayName("Admin2가 Project 1에 Tester 할당 성공")
    void TesterPermission2() throws Exception {
        // project 1에 tester 할당
        PermissionPostRequest permissionPostRequest = PermissionPostRequest.builder()
                .username("admin2")
                .password("admin2")
                .permissions(new boolean[] {false, false, true, false})
                .build();

        mockMvc.perform(post("/projects/" + project1Id + "/permissions/" + testerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionPostRequest)))
                .andExpect(status().isOk());

        assertEquals(projectService.getProject(project1Id).getMembers().get(userRepository.findById(testerId).get()), (1 << 1));
    }

    @Test
    @DisplayName("Admin3가 Project 2에 PL 할당 성공")
    void TesterPermission3() throws Exception {
        // project 2에 PL 할당
        PermissionPostRequest permissionPostRequest = PermissionPostRequest.builder()
                .username("admin3")
                .password("admin3")
                .permissions(new boolean[] {false, true, false, false})
                .build();

        mockMvc.perform(post("/projects/" + project2Id + "/permissions/" + PLId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionPostRequest)))
                .andExpect(status().isOk());

        assertEquals(projectService.getProject(project2Id).getMembers().get(userRepository.findById(PLId).get()), (1 << 2));
    }

    @Test
    @DisplayName("dev가 Project 1에 PL 할당하여 에러")
    void BadPermissionRequest() throws Exception {
        // project 1에 PL 할당
        PermissionPostRequest permissionPostRequest = PermissionPostRequest.builder()
                .username("dev")
                .password("dev")
                .permissions(new boolean[] {false, true, false, false})
                .build();

        mockMvc.perform(post("/projects/" + project1Id + "/permissions/" + PLId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionPostRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("다른 project의 admin이 요청하여 bad request error")
    void anotherProjectPermissionRequest() throws Exception {
        // project 2에 tester 할당
        PermissionPostRequest permissionPostRequest = PermissionPostRequest.builder()
                .username("admin1")
                .password("admin1")
                .permissions(new boolean[] {false, false, true, false})
                .build();

        mockMvc.perform(post("/projects/" + project2Id + "/permissions/" + testerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionPostRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("없는 계정의 할당요청")
    void ghostRequest() throws Exception {
        // project 1에 tester 할당
        PermissionPostRequest permissionPostRequest = PermissionPostRequest.builder()
                .username("ghost")
                .password("ghost")
                .permissions(new boolean[] {false, true, false, false})
                .build();

        mockMvc.perform(post("/projects/" + project1Id + "/permissions/" + testerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionPostRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("없는 계정에게 할당")
    void ghostPermission() throws Exception {
        // project 1에 ghost 할당
        PermissionPostRequest permissionPostRequest = PermissionPostRequest.builder()
                .username("admin2")
                .password("admin2")
                .permissions(new boolean[] {false, true, true, true})
                .build();

        mockMvc.perform(post("/projects/" + project1Id + "/permissions/" + 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionPostRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("없는 project에 할당")
    void ghostProjectTest() throws Exception {
        // project 1에 tester 할당
        PermissionPostRequest permissionPostRequest = PermissionPostRequest.builder()
                .username("admin1")
                .password("admin1")
                .permissions(new boolean[] {false, true, true, true})
                .build();

        mockMvc.perform(post("/projects/" + 12345 + "/permissions/" + testerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionPostRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("역할이 없도록 할당 에러")
    void noPermission() throws Exception {
        // project 1에 tester 할당
        PermissionPostRequest permissionPostRequest = PermissionPostRequest.builder()
                .username("admin1")
                .password("admin1")
                .permissions(new boolean[] {false, false, false, false})
                .build();

        mockMvc.perform(post("/projects/" + project1Id + "/permissions/" + testerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionPostRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("원래 존재하는 user를 할당하는 에러")
    void Permission() throws Exception {
        // project 1에 tester 할당
        PermissionPostRequest permissionPostRequest = PermissionPostRequest.builder()
                .username("admin1")
                .password("admin1")
                .permissions(new boolean[] {true, true, false, false})
                .build();

        mockMvc.perform(post("/projects/" + project1Id + "/permissions/" + admin2Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionPostRequest)))
                .andExpect(status().isBadRequest());
    }
}
