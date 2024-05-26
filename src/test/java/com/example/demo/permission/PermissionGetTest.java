package com.example.demo.permission;

import com.example.demo.dto.Permission.PermissionPatchRequest;
import com.example.demo.dto.Permission.PermissionPostRequest;
import com.example.demo.dto.project.ProjectPostRequest;
import com.example.demo.dto.user.UserSignupRequest;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
public class PermissionGetTest {
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
    @DisplayName("admin1이 admin2의 permission get 성공")
    void getPermission1() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/projects/" + project1Id + "/permissions/" + admin2Id)
                        .param("username", "admin1")
                        .param("password", "admin1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        boolean[] permissions = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), boolean[].class);
        assertTrue(permissions[0]);
        assertFalse(permissions[1]);
        assertFalse(permissions[2]);
        assertFalse(permissions[3]);
    }

    @Test
    @DisplayName("admin2가 admin1의 permission get 성공")
    void getPermission2() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/projects/" + project1Id + "/permissions/" + admin1Id)
                        .param("username", "admin2")
                        .param("password", "admin2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        boolean[] permissions = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), boolean[].class);
        assertTrue(permissions[0]);
        assertFalse(permissions[1]);
        assertFalse(permissions[2]);
        assertFalse(permissions[3]);
    }

    @Test
    @DisplayName("admin3이 dev의 permission get 성공")
    void getPermission3() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/projects/" + project2Id + "/permissions/" + devId)
                        .param("username", "admin3")
                        .param("password", "admin3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        boolean[] permissions = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), boolean[].class);
        assertFalse(permissions[0]);
        assertFalse(permissions[1]);
        assertFalse(permissions[2]);
        assertTrue(permissions[3]);
    }

    @Test
    @DisplayName("dev가 admin권한을 얻은 후 admin3의 permission get 성공")
    void getPermission4() throws Exception {
        PermissionPatchRequest permissionPatchRequest = PermissionPatchRequest.builder()
                .username("admin3")
                .password("admin3")
                .permissions(new boolean[] {true, false, true, false})
                .build();

        mockMvc.perform(patch("/projects/" + project2Id + "/permissions/" + devId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionPatchRequest)))
                .andExpect(status().isOk());

        MvcResult mvcResult = this.mockMvc.perform(get("/projects/" + project2Id + "/permissions/" + admin3Id)
                        .param("username", "dev")
                        .param("password", "dev")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        boolean[] permissions = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), boolean[].class);
        assertTrue(permissions[0]);
        assertFalse(permissions[1]);
        assertFalse(permissions[2]);
        assertFalse(permissions[3]);
    }

    @Test
    @DisplayName("권한이 없는 경우")
    void nonAdminGetPermission() throws Exception {
        this.mockMvc.perform(get("/projects/" + project2Id + "/permissions/" + admin3Id)
                        .param("username", "dev")
                        .param("password", "dev")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("project에 없는 사람이 요청하는 에러")
    void otherProjectAdmin() throws Exception {
        this.mockMvc.perform(get("/projects/" + project2Id + "/permissions/" + devId)
                        .param("username", "admin1")
                        .param("password", "admin1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("ghost user의 permission get 에러")
    void ghostUserGetPermission() throws Exception {
        this.mockMvc.perform(get("/projects/" + project1Id + "/permissions/" + admin1Id)
                        .param("username", "12345")
                        .param("password", "12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("project가 존재하지 않는 경우")
    void nonProjectGetRequest() throws Exception {
        this.mockMvc.perform(get("/projects/" + 12345 + "/permissions/" + admin2Id)
                        .param("username", "admin1")
                        .param("password", "admin1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("ghost user의 permission을 요청한 경우")
    void getGhostPermission() throws Exception {
        this.mockMvc.perform(get("/projects/" + project1Id + "/permissions/" + 12345)
                        .param("username", "admin1")
                        .param("password", "admin1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("admin2가 admin권한을 뺏기고 permission get error")
    void rejectAdminGetPermissionRequest() throws Exception {
        PermissionPatchRequest permissionPatchRequest = PermissionPatchRequest.builder()
                .username("admin1")
                .password("admin1")
                .permissions(new boolean[] {false, false, true, false})
                .build();

        mockMvc.perform(patch("/projects/" + project1Id + "/permissions/" + admin2Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionPatchRequest)))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/projects/" + project1Id + "/permissions/" + admin1Id)
                        .param("username", "admin2")
                        .param("password", "admin2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
