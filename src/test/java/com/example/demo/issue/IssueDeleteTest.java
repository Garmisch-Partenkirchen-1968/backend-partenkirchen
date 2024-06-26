package com.example.demo.issue;

import com.example.demo.dto.Permission.PermissionPostRequest;
import com.example.demo.dto.issue.IssueDeleteRequest;
import com.example.demo.dto.issue.IssuePostRequest;
import com.example.demo.dto.issue.IssuePostResponse;
import com.example.demo.dto.project.ProjectPostRequest;
import com.example.demo.dto.user.UserSignupRequest;
import com.example.demo.entity.Issue;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.permission.PermissionPostTest;
import com.example.demo.repository.IssueRepository;
import com.example.demo.service.PermissionService;
import com.example.demo.service.ProjectService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs
public class IssueDeleteTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private IssueRepository issueRepository;

    private Long projectId;
    private Issue defaultIssue;

    @BeforeEach
    void init() throws Exception {
        // project를 생성할 유저 생성
        UserSignupRequest admin = UserSignupRequest.builder()
                .username("admin")
                .password("admin")
                .build();
        userService.signUpUser(admin);

        // issue를 생성할 유저 생성
        UserSignupRequest tester1 = UserSignupRequest.builder()
                .username("tester1")
                .password("tester1")
                .build();
        Long tester1Id = userService.signUpUser(tester1).getId();

        // another issue를 생성할 유저 생성
        UserSignupRequest tester2 = UserSignupRequest.builder()
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
        Long anotherProjectId = projectService.createProject(anotherProjectCreater).getId();

        // admin이 tester1에게 tester권한 부여
        PermissionPostRequest permissionRequest = PermissionPostRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, true, false})
                .build();
        permissionService.addPermission(projectId, tester1Id, permissionRequest);

        // default issue 생성
        IssuePostRequest issuePostRequest = IssuePostRequest.builder()
                .username("tester1")
                .password("tester1")
                .title("default issue")
                .priority(IssuePriority.MINOR)
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
        PermissionPostRequest anotherPermissionRequest = PermissionPostRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, true, false})
                .build();
        permissionService.addPermission(anotherProjectId, tester2Id, anotherPermissionRequest);
    }

    @Test
    @DisplayName("delete issue 성공")
    void deleteIssue() throws Exception {
        IssueDeleteRequest issueDeleteRequest = IssueDeleteRequest.builder()
                .username("tester1")
                .password("tester1")
                .build();

        this.mockMvc.perform(delete("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(issueDeleteRequest)))
                .andExpect(status().isOk())
                .andDo(document("issues/delete/success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));

        // 잘 지워졌는지 확인
        Optional<Issue> optionalIssue = issueRepository.findById(defaultIssue.getId());
        assertTrue(optionalIssue.isEmpty());
    }

    @Test
    @DisplayName("권한이 없는 사람이 delete issue")
    void deleteIssueWithoutPermission() throws Exception {
        IssueDeleteRequest issueDeleteRequest = IssueDeleteRequest.builder()
                .username("tester2")
                .password("tester2")
                .build();

        this.mockMvc.perform(delete("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueDeleteRequest)))
                .andExpect(status().isForbidden());

        // 안 지워졌는지 확인
        Optional<Issue> optionalIssue = issueRepository.findById(defaultIssue.getId());
        assertTrue(optionalIssue.isPresent());
    }

    @Test
    @DisplayName("권한이 없는 사람이 delete issue 2")
    void deleteIssueWithoutPermission2() throws Exception {
        IssueDeleteRequest issueDeleteRequest = IssueDeleteRequest.builder()
                .username("ghost")
                .password("ghost")
                .build();

        this.mockMvc.perform(delete("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueDeleteRequest)))
                .andExpect(status().isUnauthorized());

        // 안 지워졌는지 확인
        Optional<Issue> optionalIssue = issueRepository.findById(defaultIssue.getId());
        assertTrue(optionalIssue.isPresent());
    }
}