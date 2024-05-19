package com.example.demo.issue;

import com.example.demo.dto.issue.IssuePatchRequest;
import com.example.demo.dto.issue.IssuePostRequest;
import com.example.demo.dto.issue.IssuePostResponse;
import com.example.demo.dto.project.PermissionRequest;
import com.example.demo.dto.project.ProjectCreater;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
public class IssuePatchTest {
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

        // PL1 생성
        User PL1 = User.builder()
                .username("PL1")
                .password("PL1")
                .build();
        Long PL1Id = userService.signUpUser(PL1).getId();

        // dev1 생성
        User dev1 = User.builder()
                .username("dev1")
                .password("dev1")
                .build();
        Long dev1Id = userService.signUpUser(dev1).getId();

        // another issue를 생성할 유저 생성
        User tester2 = User.builder()
                .username("tester2")
                .password("tester2")
                .build();
        Long tester2Id = userService.signUpUser(tester2).getId();

        // 프로젝트 생성
        ProjectCreater projectCreater = ProjectCreater.builder()
                .username("admin")
                .password("admin")
                .projectName("new project!")
                .build();
        projectId = projectService.createProject(projectCreater).getId();

        // 다른 프로젝트 생성
        ProjectCreater anotherProjectCreater = ProjectCreater.builder()
                .username("admin")
                .password("admin")
                .projectName("another project!")
                .build();
        anotherProjectId = projectService.createProject(anotherProjectCreater).getId();

        // admin이 tester1에게 tester권한 부여
        PermissionRequest permissionRequest = PermissionRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, true, false})
                .build();
        projectService.addPermission(projectId, tester1Id, permissionRequest);

        // admin이 PL1에게 tester권한 부여
        PermissionRequest PL1PermissionRequest = PermissionRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, true, false, false})
                .build();
        projectService.addPermission(projectId, PL1Id, PL1PermissionRequest);

        // admin이 dev1에게 tester권한 부여
        PermissionRequest dev1PermissionRequest = PermissionRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, false, true})
                .build();
        projectService.addPermission(projectId, dev1Id, dev1PermissionRequest);

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
    @DisplayName("issue 이름 바꾸기")
    void patchIssue() throws Exception {
        // title 수정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("tester1")
                .password("tester1")
                .title("new issue name")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isOk());

        // 수정된 issue 검색
        Optional<Issue> optionalIssue = issueRepository.findById(defaultIssue.getId());
        assertTrue(optionalIssue.isPresent());
        Issue issue = optionalIssue.get();

        // 수정된 이슈 검사
        assertEquals("new issue name", issue.getTitle());
        assertEquals(IssueStatus.NEW, issue.getStatus());
        assertEquals(IssuePriority.MEDIUM, issue.getPriority());
        assertNull(issue.getAssignee());
    }

    @Test
    @DisplayName("권한 없는 사람이 issue 이름 바꾸기")
    void patchIssueWithWrongPermission() throws Exception {
        // title 수정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("tester2")
                .password("tester2")
                .title("new issue name")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PL1이 dev1을 assignee로 지정")
    void assignDev1ToAssignee() throws Exception {
        // title 수정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("PL1")
                .password("PL1")
                .assignee("dev1")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isOk());

        // 수정된 issue 검색
        Optional<Issue> optionalIssue = issueRepository.findById(defaultIssue.getId());
        assertTrue(optionalIssue.isPresent());
        Issue issue = optionalIssue.get();

        // 수정된 이슈 검사
        assertEquals("default issue", issue.getTitle());
        assertEquals(IssueStatus.ASSIGNED, issue.getStatus());
        assertEquals(IssuePriority.MEDIUM, issue.getPriority());
        assertEquals("dev1", issue.getAssignee().getUsername());
    }

    @Test
    @DisplayName("권한이 없는 사람이 dev1을 assignee로 지정")
    void foreignAssignDev1ToAssignee() throws Exception {
        // title 수정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("tester2")
                .password("tester2")
                .assignee("dev1")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isOk());

        // 수정안된 issue 검색
        Optional<Issue> optionalIssue = issueRepository.findById(defaultIssue.getId());
        assertTrue(optionalIssue.isPresent());
        Issue issue = optionalIssue.get();

        // 수정안된 이슈 검사
        assertEquals("default issue", issue.getTitle());
        assertEquals(IssueStatus.NEW, issue.getStatus());
        assertEquals(IssuePriority.MEDIUM, issue.getPriority());
        assertNull(issue.getAssignee());
    }

    @Test
    @DisplayName("PL1이 없는 사람을 assignee로 지정")
    void assignGhostToAssignee() throws Exception {
        // title 수정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("PL1")
                .password("PL1")
                .assignee("ghost")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PL1이 여러가지를 한 번에 바꾸려고 함")
    void doMultiplePatch() throws Exception {
        // title 수정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("PL1")
                .password("PL1")
                .title("new issue name")
                .assignee("dev1")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("dev1이 자신에게 할당된 이슈를 fixed로 바꿈")
    void patchIssueToFixed() throws Exception {
        // assignee 배정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("PL1")
                .password("PL1")
                .assignee("dev1")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isOk());

        // fixed로 수정
        IssuePatchRequest fixedPatchRequest = IssuePatchRequest.builder()
                .username("dev1")
                .password("dev1")
                .status(IssueStatus.FIXED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedPatchRequest)))
                .andExpect(status().isOk());


        // 수정된 issue 검색
        Optional<Issue> optionalIssue = issueRepository.findById(defaultIssue.getId());
        assertTrue(optionalIssue.isPresent());
        Issue issue = optionalIssue.get();

        // 수정된 이슈 검사
        assertEquals("default issue", issue.getTitle());
        assertEquals(IssueStatus.FIXED, issue.getStatus());
        assertEquals(IssuePriority.MEDIUM, issue.getPriority());
        assertEquals("dev1", issue.getAssignee().getUsername());
        assertEquals("dev1", issue.getFixer().getUsername());
    }

    @Test
    @DisplayName("권한이 없는 사람이 fixed로 바꾸려 함")
    void patchIssueToFixedWithWrongPermission() throws Exception {
        // assignee 배정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("PL1")
                .password("PL1")
                .assignee("dev1")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isOk());

        // fixed로 수정
        IssuePatchRequest fixedPatchRequest = IssuePatchRequest.builder()
                .username("admin")
                .password("admin")
                .status(IssueStatus.FIXED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedPatchRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("dev1이 이미 fixed인걸 또 fixed로 바꿈")
    void patchIssueToFixedAlreadyFixed() throws Exception {
        // assignee 배정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("PL1")
                .password("PL1")
                .assignee("dev1")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isOk());

        // fixed로 수정
        IssuePatchRequest fixedPatchRequest = IssuePatchRequest.builder()
                .username("dev1")
                .password("dev1")
                .status(IssueStatus.FIXED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedPatchRequest)))
                .andExpect(status().isOk());

        // 또 바꿈
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedPatchRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("dev1이 issue status를 resolved로 바꾸려 함")
    void dev1PatchIssueToResolve() throws Exception {
        // assignee 배정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("PL1")
                .password("PL1")
                .assignee("dev1")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isOk());

        // fixed로 수정
        IssuePatchRequest fixedPatchRequest = IssuePatchRequest.builder()
                .username("dev1")
                .password("dev1")
                .status(IssueStatus.RESOLVED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedPatchRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("tester1가 issue를 resolved로 바꿈")
    void tester1MakeIssueToResolved() throws Exception {
        // assignee 배정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("PL1")
                .password("PL1")
                .assignee("dev1")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isOk());

        // fixed로 수정
        IssuePatchRequest fixedPatchRequest = IssuePatchRequest.builder()
                .username("dev1")
                .password("dev1")
                .status(IssueStatus.FIXED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedPatchRequest)))
                .andExpect(status().isOk());

        // resolved로 수정
        IssuePatchRequest resolvedPatchRequest = IssuePatchRequest.builder()
                .username("tester1")
                .password("tester1")
                .status(IssueStatus.RESOLVED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resolvedPatchRequest)))
                .andExpect(status().isOk());

        // 수정된 issue 검색
        Optional<Issue> optionalIssue = issueRepository.findById(defaultIssue.getId());
        assertTrue(optionalIssue.isPresent());
        Issue issue = optionalIssue.get();

        // 수정된 이슈 검사
        assertEquals("default issue", issue.getTitle());
        assertEquals(IssueStatus.RESOLVED, issue.getStatus());
        assertEquals(IssuePriority.MEDIUM, issue.getPriority());
        assertEquals("dev1", issue.getAssignee().getUsername());
        assertEquals("dev1", issue.getFixer().getUsername());
    }

    @Test
    @DisplayName("권한이 없는 사람이 issue를 resolved로 바꿈")
    void tester1MakeIssueToResolvedWithoutPermission() throws Exception {
        // assignee 배정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("PL1")
                .password("PL1")
                .assignee("dev1")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isOk());

        // fixed로 수정
        IssuePatchRequest fixedPatchRequest = IssuePatchRequest.builder()
                .username("dev1")
                .password("dev1")
                .status(IssueStatus.FIXED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedPatchRequest)))
                .andExpect(status().isOk());

        IssuePatchRequest resolvedPatchRequest = IssuePatchRequest.builder()
                .username("tester2")
                .password("tester2")
                .status(IssueStatus.RESOLVED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resolvedPatchRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PL1이 issue를 closed로 바꿈")
    void PL1MakeIssueToClosed() throws Exception {
        // assignee 배정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("PL1")
                .password("PL1")
                .assignee("dev1")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isOk());

        // fixed로 수정
        IssuePatchRequest fixedPatchRequest = IssuePatchRequest.builder()
                .username("dev1")
                .password("dev1")
                .status(IssueStatus.FIXED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedPatchRequest)))
                .andExpect(status().isOk());

        // resolved로 수정
        IssuePatchRequest resolvedPatchRequest = IssuePatchRequest.builder()
                .username("tester1")
                .password("tester1")
                .status(IssueStatus.RESOLVED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resolvedPatchRequest)))
                .andExpect(status().isOk());

        // closed로 수정
        IssuePatchRequest closedPatchRequest = IssuePatchRequest.builder()
                .username("PL1")
                .password("PL1")
                .status(IssueStatus.CLOSED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(closedPatchRequest)))
                .andExpect(status().isOk());

        // 수정된 issue 검색
        Optional<Issue> optionalIssue = issueRepository.findById(defaultIssue.getId());
        assertTrue(optionalIssue.isPresent());
        Issue issue = optionalIssue.get();

        // 수정된 이슈 검사
        assertEquals("default issue", issue.getTitle());
        assertEquals(IssueStatus.CLOSED, issue.getStatus());
        assertEquals(IssuePriority.MEDIUM, issue.getPriority());
        assertEquals("dev1", issue.getAssignee().getUsername());
        assertEquals("dev1", issue.getFixer().getUsername());
    }

    @Test
    @DisplayName("권한 없는 사람이 issue를 closed로 바꿈")
    void foreignMakeIssueToClosed() throws Exception {
        // assignee 배정
        IssuePatchRequest issuePatchRequest = IssuePatchRequest.builder()
                .username("PL1")
                .password("PL1")
                .assignee("dev1")
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuePatchRequest)))
                .andExpect(status().isOk());

        // fixed로 수정
        IssuePatchRequest fixedPatchRequest = IssuePatchRequest.builder()
                .username("dev1")
                .password("dev1")
                .status(IssueStatus.FIXED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedPatchRequest)))
                .andExpect(status().isOk());

        // resolved로 수정
        IssuePatchRequest resolvedPatchRequest = IssuePatchRequest.builder()
                .username("tester1")
                .password("tester1")
                .status(IssueStatus.RESOLVED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resolvedPatchRequest)))
                .andExpect(status().isOk());

        // closed로 수정
        IssuePatchRequest closedPatchRequest = IssuePatchRequest.builder()
                .username("admin")
                .password("admin")
                .status(IssueStatus.CLOSED)
                .build();
        this.mockMvc.perform(patch("/projects/" + projectId + "/issues/" + defaultIssue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(closedPatchRequest)))
                .andExpect(status().isForbidden());
    }
}
