package com.example.demo.issue;

import com.example.demo.dto.issue.IssuePostRequest;
import com.example.demo.dto.issue.IssuePostResponse;
import com.example.demo.dto.project.ProjectPostRequest;
import com.example.demo.entity.Issue;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.entity.enumerate.IssueStatus;
import com.example.demo.repository.IssueRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
public class IssueGetTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private IssueRepository issueRepository;

    private Long projectId;
    private Issue defaultIssue;

    private Long anotherProjectId;
    private Issue anotherIssue;

    @BeforeEach
    void init() throws Exception {
        // projeect를 생성할 유저 생성
        User admin = User.builder()
                .username("admin")
                .password("admin")
                .build();
        userService.signUpUser(admin);

        // issue를 생성할 유저 생성
        User tester1 = User.builder()
                .username("tester1")
                .password("tester1")
                .build();
        Long tester1Id = userService.signUpUser(tester1).getId();

        // another issue를 생성할 유저 생성
        User tester2 = User.builder()
                .username("tester2")
                .password("tester2")
                .build();
        Long tester2Id = userService.signUpUser(tester2).getId();

        // 프로젝트 생성
        ProjectPostRequest projectCreater = ProjectPostRequest.builder()
                .username("admin")
                .password("admin")
                .name("new project!")
                .description("some description")
                .build();
        projectId = projectService.createProject(projectCreater).getId();

        // 다른 프로젝트 생성
        ProjectPostRequest anotherProjectCreater = ProjectPostRequest.builder()
                .username("admin")
                .password("admin")
                .name("another project!")
                .description("another description")
                .build();
        anotherProjectId = projectService.createProject(anotherProjectCreater).getId();

        // admin이 tester1에게 tester권한 부여
        PermissionRequest permissionRequest = PermissionRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, true, false})
                .build();
        projectService.addPermission(projectId, tester1Id, permissionRequest);

        // default issue 생성
        IssuePostRequest issuePostRequest = IssuePostRequest.builder()
                .username("tester1")
                .password("tester1")
                .title("default issue")
                .priority(IssuePriority.MEDIUM)
                .build();
        MvcResult mvcResult = this.mockMvc.perform(post("/projects/" + projectId + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePostRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        IssuePostResponse issuePostResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), IssuePostResponse.class);
        Optional<Issue> optionalIssue = issueRepository.findById(issuePostResponse.getId());
        assertTrue(optionalIssue.isPresent());
        defaultIssue = optionalIssue.get();

        // admin이 tester2에게 tester권한 부여 (another project에서)
        PermissionRequest anotherPermissionRequest = PermissionRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, true, false})
                .build();
        projectService.addPermission(anotherProjectId, tester2Id, anotherPermissionRequest);

        // another issue 생성
        IssuePostRequest anotherIssuePostRequest = IssuePostRequest.builder()
                .username("tester1")
                .password("tester1")
                .title("another issue")
                .priority(IssuePriority.MEDIUM)
                .build();
        MvcResult anotherMvcResult = this.mockMvc.perform(post("/projects/" + projectId + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(anotherIssuePostRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        IssuePostResponse anotherIssuePostResponse = objectMapper.readValue(anotherMvcResult.getResponse().getContentAsString(), IssuePostResponse.class);
        Optional<Issue> anotherOptionalIssue = issueRepository.findById(anotherIssuePostResponse.getId());
        assertTrue(anotherOptionalIssue.isPresent());
        anotherIssue = optionalIssue.get();
    }

    @Test
    @DisplayName("issue get 성공")
    void getIssue() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .param("username", "tester1")
                        .param("password", "tester1"))
                .andExpect(status().isOk())
                .andReturn();

        Issue issue = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue.class);
        assertEquals(issue.getTitle(), "default issue");
        assertEquals(issue.getPriority(), IssuePriority.MEDIUM);
        assertEquals(issue.getStatus(), IssueStatus.NEW);
    }

    @Test
    @DisplayName("admin이 issue 검색")
    void getIssueAsAdmin() throws Exception {
        this.mockMvc.perform(get("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .param("username", "admin")
                        .param("password", "admin"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트의 이슈 검색")
    void getIssueInNotExistProject() throws Exception {
        this.mockMvc.perform(get("/projects/" + 982734 + "/issues/" + 123124)
                        .param("username", "tester1")
                        .param("password", "tester1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("member가 아닌 다른 project의 이슈 get")
    void getIssueInAnotherProject() throws Exception {
        this.mockMvc.perform(get("/projects/" + anotherProjectId + "/issues/" + anotherIssue.getId())
                        .param("username", "tester1")
                        .param("password", "tester1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 이슈 get")
    void getIssueWithWrongPassword() throws Exception {
        this.mockMvc.perform(get("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .param("username", "tester1")
                        .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("없는 issue get")
    void getNotExistIssue() throws Exception {
        this.mockMvc.perform(get("/projects/" + projectId + "/issues/" + 93843)
                        .param("username", "tester1")
                        .param("password", "tester1"))
                .andExpect(status().isForbidden());
    }
}
