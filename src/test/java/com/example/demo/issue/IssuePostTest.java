package com.example.demo.issue;

import com.example.demo.dto.issue.IssuePostRequest;
import com.example.demo.dto.issue.IssuePostResponse;
import com.example.demo.dto.project.PermissionRequest;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
public class IssuePostTest {
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

    private Long tester1Id;
    private Long projectId;

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
        tester1Id = userService.signUpUser(tester1).getId();

        // 프로젝트 생성
        ProjectPostRequest projectCreater = ProjectPostRequest.builder()
                .username("admin")
                .password("admin")
                .projectName("new project!")
                .projectDescription("some description")
                .build();
        projectId = projectService.createProject(projectCreater).getId();

        // admin이 tester1에게 tester권한 부여
        PermissionRequest permissionRequest = PermissionRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, true, false})
                .build();
        projectService.addPermission(projectId, tester1Id, permissionRequest);
    }

    @Test
    @DisplayName("issue 생성 성공")
    void issuePostTest() throws Exception {
        // issue 생성
        IssuePostRequest issuePostRequest = IssuePostRequest.builder()
                .username("tester1")
                .password("tester1")
                .title("new issue")
                .priority(IssuePriority.CRITICAL)
                .build();
        MvcResult mvcResult = this.mockMvc.perform(post("/projects/" + projectId + "/issues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(issuePostRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        IssuePostResponse issuePostResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), IssuePostResponse.class);
        Optional<Issue> optionalIssue = issueRepository.findById(issuePostResponse.getId());

        assertTrue(optionalIssue.isPresent());
        Issue issue = optionalIssue.get();
        assertEquals("new issue", issue.getTitle());
        assertEquals(IssuePriority.CRITICAL, issue.getPriority());
        assertEquals(tester1Id, issue.getReporter().getId());
        assertEquals(IssueStatus.NEW, issue.getStatus());
        assertTrue(issue.getReportedDate().isBefore(LocalDateTime.now()));
        assertNull(issue.getFixer());
        assertNull(issue.getAssignee());
    }

    @Test
    @DisplayName("잘못 된 비밀번호로 이슈 생성 시도")
    void postIssueWithWrongPassword() throws Exception {
        // issue 생성
        IssuePostRequest issuePostRequest = IssuePostRequest.builder()
                .username("tester1")
                .password("wrongpassword")
                .title("new issue with wrong password")
                .priority(IssuePriority.CRITICAL)
                .build();
        this.mockMvc.perform(post("/projects/" + projectId + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePostRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("권한 없는 사람이 이슈 생성 시도")
    void postIssueWithoutPermission() throws Exception {
        // issue 생성
        IssuePostRequest issuePostRequest = IssuePostRequest.builder()
                .username("admin")
                .password("admin")
                .title("new issue without permission")
                .priority(IssuePriority.CRITICAL)
                .build();
        this.mockMvc.perform(post("/projects/" + projectId + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePostRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("빈 제목으로 이슈 생성 시도")
    void postIssueWithEmptyTitle() throws Exception {
        // issue 생성
        IssuePostRequest issuePostRequest = IssuePostRequest.builder()
                .username("tester1")
                .password("tester1")
                .title("")
                .priority(IssuePriority.CRITICAL)
                .build();
        this.mockMvc.perform(post("/projects/" + projectId + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePostRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("priority없이 이슈 생성 시도")
    void postIssueWithoutPriority() throws Exception {
        // issue 생성
        this.mockMvc.perform(post("/projects/" + projectId + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":  \"tester1\", \"password\": \"tester1\",  \"title\":  \"new issue without priority\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("계정 없이 이슈 생성 시도")
    void postIssueWithoutAccount() throws Exception {
        // issue 생성
        IssuePostRequest issuePostRequest = IssuePostRequest.builder()
                .title("new issue without account")
                .priority(IssuePriority.CRITICAL)
                .build();
        this.mockMvc.perform(post("/projects/" + projectId + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePostRequest)))
                .andExpect(status().isUnauthorized());
    }
}
