package com.example.demo.permission;

import com.example.demo.dto.Permission.PermissionDeleteRequest;
import com.example.demo.dto.Permission.PermissionPatchRequest;
import com.example.demo.dto.Permission.PermissionPostRequest;
import com.example.demo.dto.project.ProjectPostRequest;
import com.example.demo.dto.user.UserSignupRequest;
import com.example.demo.repository.ProjectRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
public class PermissionDeleteTest {
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

    @Autowired
    private UserRepository userRepository;

    Long project1Id;
    Long project2Id;

    Long admin1Id;
    Long admin2Id;
    Long admin3Id;
    Long PLId;
    Long testerId;
    Long devId;
    @Autowired
    private ProjectRepository projectRepository;

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
                .permissions(new boolean[] {false, false, false, true})
                .build();
        permissionService.addPermission(project2Id, devId, permissionPostRequest2);
    }

    @Test
    @DisplayName("admin1이 admin2의 permission 삭제 성공")
    void DeletePermission1() throws Exception {
        // admin1이 admin2 삭제
        PermissionDeleteRequest permissionDeleteRequest = PermissionDeleteRequest.builder()
                .username("admin1")
                .password("admin1")
                .build();
        mockMvc.perform(delete("/projects/" + project1Id + "/permissions/" + admin2Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionDeleteRequest)))
                .andExpect(status().isOk());

        assertNull(projectRepository.findById(project1Id).get().getMembers().get(userRepository.findById(admin2Id).get()));
    }

    @Test
    @DisplayName("admin2가 admin1의 permission 삭제 성공")
    void DeletePermission2() throws Exception {
        // admin2가 admin1 삭제
        PermissionDeleteRequest permissionDeleteRequest = PermissionDeleteRequest.builder()
                .username("admin2")
                .password("admin2")
                .build();
        mockMvc.perform(delete("/projects/" + project1Id + "/permissions/" + admin1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionDeleteRequest)))
                .andExpect(status().isOk());

        assertNull(projectRepository.findById(project1Id).get().getMembers().get(userRepository.findById(admin1Id).get()));
    }

    @Test
    @DisplayName("admin3가 dev의 permission 삭제 성공")
    void DeletePermission3() throws Exception {
        // admin3가 dev 삭제
        PermissionDeleteRequest permissionDeleteRequest = PermissionDeleteRequest.builder()
                .username("admin3")
                .password("admin3")
                .build();
        mockMvc.perform(delete("/projects/" + project2Id + "/permissions/" + devId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionDeleteRequest)))
                .andExpect(status().isOk());

        assertNull(projectRepository.findById(project2Id).get().getMembers().get(userRepository.findById(devId).get()));
    }

    @Test
    @DisplayName("dev가 admin 권한을 얻은 후 admin3의 permission 삭제 성공")
    void DeletePermission4() throws Exception {
        // dev에게 admin 권한 부여
        PermissionPatchRequest permissionPatchRequest = PermissionPatchRequest.builder()
                .username("admin3")
                .password("admin3")
                .permissions(new boolean[] {true, false, true, false})
                .build();

        mockMvc.perform(patch("/projects/" + project2Id + "/permissions/" + devId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionPatchRequest)))
                .andExpect(status().isOk());

        // dev가 admin3 삭제
        PermissionDeleteRequest permissionDeleteRequest = PermissionDeleteRequest.builder()
                .username("dev")
                .password("dev")
                .build();
        mockMvc.perform(delete("/projects/" + project2Id + "/permissions/" + admin3Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionDeleteRequest)))
                .andExpect(status().isOk());

        assertNull(projectRepository.findById(project2Id).get().getMembers().get(userRepository.findById(admin3Id).get()));
    }

    @Test
    @DisplayName("project에 없는 사람의 delete 요청")
    void nonMemberDeleteRequest() throws Exception {
        // tester가 admin1 삭제
        PermissionDeleteRequest permissionDeleteRequest = PermissionDeleteRequest.builder()
                .username("tester")
                .password("tester")
                .build();
        mockMvc.perform(delete("/projects/" + project1Id + "/permissions/" + admin1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionDeleteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("없는 user의 delete 요청")
    void RequestOfGhost() throws Exception {
        // ghost가 admin1 삭제
        PermissionDeleteRequest permissionDeleteRequest = PermissionDeleteRequest.builder()
                .username("ghost")
                .password("ghost")
                .build();
        mockMvc.perform(delete("/projects/" + project1Id + "/permissions/" + admin1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionDeleteRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("없는 user의 delete 요청")
    void GhostDeleteRequest() throws Exception {
        // admin1이 ghost 삭제
        PermissionDeleteRequest permissionDeleteRequest = PermissionDeleteRequest.builder()
                .username("admin1")
                .password("admin1")
                .build();
        mockMvc.perform(delete("/projects/" + project1Id + "/permissions/" + 12345)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionDeleteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("권한 없는 사람의 delete request")
    void nonPermissionRequest() throws Exception {
        // dev가 admin1 삭제
        PermissionDeleteRequest permissionDeleteRequest = PermissionDeleteRequest.builder()
                .username("dev")
                .password("dev")
                .build();
        mockMvc.perform(delete("/projects/" + project2Id + "/permissions/" + admin3Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionDeleteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("project가 없는 경우")
    void nonProjectRequest() throws Exception {
        // admin1이 admin2를 non project에서 삭제
        PermissionDeleteRequest permissionDeleteRequest = PermissionDeleteRequest.builder()
                .username("admin1")
                .password("admin1")
                .build();
        mockMvc.perform(delete("/projects/" + 12345 + "/permissions/" + admin2Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionDeleteRequest)))
                .andExpect(status().isBadRequest());
    }
}
