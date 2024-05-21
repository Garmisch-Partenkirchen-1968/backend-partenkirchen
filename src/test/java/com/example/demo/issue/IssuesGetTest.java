package com.example.demo.issue;

import com.example.demo.dto.issue.IssuesGetRequest;
import com.example.demo.dto.project.PermissionRequest;
import com.example.demo.dto.project.ProjectPostRequest;
import com.example.demo.entity.Issue;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.entity.enumerate.IssueStatus;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IssueService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
public class IssuesGetTest {
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private Long projectId;
    @Autowired
    private IssueService issueService;

    void addIssue(String title, String reporterUsername, String fixerUsername, String assigneeUsername, IssuePriority priority, IssueStatus status) {
        Optional<User> optionalReporter = userRepository.getReferenceByUsername(reporterUsername);
        assertTrue(optionalReporter.isPresent());
        User reporter = optionalReporter.get();

        Optional<User> optionalFixer = userRepository.getReferenceByUsername(fixerUsername);
        User fixer = optionalFixer.orElse(null);

        Optional<User> optionalAssignee = userRepository.getReferenceByUsername(assigneeUsername);
        User assignee = optionalAssignee.orElse(null);

        Issue issue = Issue.builder()
                .title(title)
                .reporter(reporter)
                .fixer(fixer)
                .assignee(assignee)
                .priority(priority)
                .status(status)
                .build();
        issueRepository.save(issue);

        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Project project = optionalProject.orElse(null);
        project.getIssues().add(issue);
        projectRepository.save(project);
    }

    // admin
    // PL1
    // tester1, tester2
    // dev1, dev2, dev3, dev,4
    // foreign
    @BeforeEach
    void init() {
        // create admin
        User admin = User.builder()
                .username("admin")
                .password("admin")
                .build();
        userService.signUpUser(admin);

        // create PL1
        User PL1 = User.builder()
                .username("PL1")
                .password("PL1")
                .build();
        Long PL1Id = userService.signUpUser(PL1).getId();

        // create tester1
        User tester1 = User.builder()
                .username("tester1")
                .password("tester1")
                .build();
        Long tester1Id = userService.signUpUser(tester1).getId();

        // create tester2
        User tester2 = User.builder()
                .username("tester2")
                .password("tester2")
                .build();
        Long tester2Id = userService.signUpUser(tester2).getId();

        // create dev1
        User dev1 = User.builder()
                .username("dev1")
                .password("dev1")
                .build();
        Long dev1Id = userService.signUpUser(dev1).getId();

        // create dev2
        User dev2 = User.builder()
                .username("dev2")
                .password("dev2")
                .build();
        Long dev2Id = userService.signUpUser(dev2).getId();

        // create dev3
        User dev3 = User.builder()
                .username("dev3")
                .password("dev3")
                .build();
        Long dev3Id = userService.signUpUser(dev3).getId();

        // create dev4
        User dev4 = User.builder()
                .username("dev4")
                .password("dev4")
                .build();
        Long dev4Id = userService.signUpUser(dev4).getId();

        // create foreign
        User foreign = User.builder()
                .username("foreign")
                .password("foreign")
                .build();
        userService.signUpUser(foreign);

        // create project
        ProjectPostRequest projectCreater = ProjectPostRequest.builder()
                .username("admin")
                .password("admin")
                .projectName("new project!")
                .projectDescription("some description")
                .build();
        projectId = projectService.createProject(projectCreater).getId();

        // give PL permission to PL1
        PermissionRequest PLPermissionRequest = PermissionRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, true, false, false})
                .build();
        projectService.addPermission(projectId, PL1Id, PLPermissionRequest);

        // give tester permission to tester1
        PermissionRequest testerPermissionRequest = PermissionRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, true, false})
                .build();
        projectService.addPermission(projectId, tester1Id, testerPermissionRequest);

        // give tester permission to tester2
        projectService.addPermission(projectId, tester2Id, testerPermissionRequest);

        // give tester permission to dev1
        PermissionRequest dev1PermissionRequest = PermissionRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, false, true})
                .build();
        projectService.addPermission(projectId, dev1Id, dev1PermissionRequest);

        // give tester permission to dev2
        projectService.addPermission(projectId, dev2Id, dev1PermissionRequest);

        // give tester permission to dev2
        projectService.addPermission(projectId, dev3Id, dev1PermissionRequest);

        // give tester permission to dev2
        projectService.addPermission(projectId, dev4Id, dev1PermissionRequest);

        addIssue("alpha1", "tester1", "", "", IssuePriority.LOW, IssueStatus.NEW);
        addIssue("beta1", "tester2", "", "dev1", IssuePriority.MEDIUM, IssueStatus.ASSIGNED);
        addIssue("gamma1", "tester1", "dev1", "dev1", IssuePriority.HIGH, IssueStatus.FIXED);
        addIssue("delta1", "tester2", "dev1", "dev1", IssuePriority.CRITICAL, IssueStatus.RESOLVED);
        addIssue("epsilon1", "tester1", "dev1", "dev1", IssuePriority.LOW, IssueStatus.CLOSED);
        addIssue("zeta1", "tester2", "", "", IssuePriority.MEDIUM, IssueStatus.NEW);
        addIssue("alpha2", "tester1", "", "dev2", IssuePriority.HIGH, IssueStatus.ASSIGNED);
        addIssue("beta2", "tester2", "dev2", "dev2", IssuePriority.CRITICAL, IssueStatus.FIXED);
        addIssue("gamma2", "tester1", "dev2", "dev2", IssuePriority.LOW, IssueStatus.RESOLVED);
        addIssue("delta2", "tester2", "dev2", "dev2", IssuePriority.MEDIUM, IssueStatus.CLOSED);
        addIssue("epsilon2", "tester1", "", "", IssuePriority.HIGH, IssueStatus.NEW);
        addIssue("zeta2", "tester2", "", "dev3", IssuePriority.CRITICAL, IssueStatus.ASSIGNED);
        addIssue("alpha3", "tester1", "dev3", "dev3", IssuePriority.LOW, IssueStatus.FIXED);
        addIssue("beta3", "tester2", "dev3", "dev3", IssuePriority.MEDIUM, IssueStatus.RESOLVED);
        addIssue("gamma3", "tester1", "dev3", "dev3", IssuePriority.HIGH, IssueStatus.CLOSED);
        addIssue("delta3", "tester2", "", "", IssuePriority.CRITICAL, IssueStatus.NEW);
        addIssue("epsilon3", "tester1", "", "dev4", IssuePriority.LOW, IssueStatus.ASSIGNED);
        addIssue("zeta3", "tester2", "dev4", "dev4", IssuePriority.MEDIUM, IssueStatus.FIXED);
        addIssue("alpha4", "tester1", "dev4", "dev4", IssuePriority.HIGH, IssueStatus.RESOLVED);
        addIssue("beta4", "tester2", "dev4", "dev4", IssuePriority.CRITICAL, IssueStatus.CLOSED);
    }

    @Test
    @DisplayName("get all issues") // 20
    void getAllIssues() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin"))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(20, issues.length);
    }

    @Test
    @DisplayName("get issues title: alpha") // 4
    void getIssueTitleAlpha() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .title("alpha")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues title: delta") // 3
    void getIssueTitleDelta() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .title("delta")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(3, issues.length);
    }

    @Test
    @DisplayName("get issues title: 2") // 6
    void getIssueTitle2() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .title("2")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(6, issues.length);
    }

    @Test
    @DisplayName("get issues title: 4") // 2
    void getIssueTitle4() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .title("4")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(2, issues.length);
    }

    @Test
    @DisplayName("get issues reporter: tester1") // 10
    void getIssueReporterTester1() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .reporter("tester1")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(10, issues.length);
    }

    @Test
    @DisplayName("get issues reporter: tester2") // 10
    void getIssueReporterTester2() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .reporter("tester2")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(10, issues.length);
    }

    @Test
    @DisplayName("get issues fixer: null") // 8
    void getIssueFixerNull() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .fixer("")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(8, issues.length);
    }

    @Test
    @DisplayName("get issues fixer: dev1") // 3
    void getIssueFixerDev1() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .fixer("dev1")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(3, issues.length);
    }

    @Test
    @DisplayName("get issues fixer: dev3") // 3
    void getIssueFixerDev3() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .fixer("dev3")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(3, issues.length);
        System.out.println(issues.toString());
    }

    @Test
    @DisplayName("get issues assignee: null") // 4
    void getIssueAssigneeNull() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .assignee("")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues assignee: dev2") // 4
    void getIssueAssigneeDev2() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .assignee("dev2")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues assignee: dev4") // 4
    void getIssueAssigneeDev4() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .assignee("dev4")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues priority: medium") // 5
    void getIssuePriorityMedium() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .priority(IssuePriority.MEDIUM)
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(5, issues.length);
    }

    @Test
    @DisplayName("get issues priority: critical") // 5
    void getIssuePriorityCritical() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .priority(IssuePriority.CRITICAL)
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(5, issues.length);
    }

    @Test
    @DisplayName("get issues status: new") // 4
    void getIssueStatusNew() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .status(IssueStatus.NEW)
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues status: fixed") // 4
    void getIssueStatusFixed() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .status(IssueStatus.FIXED)
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues title: beta, reporter: tester1") // 0
    void getIssueTitleBetaReporterTester1() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .title("beta")
                .reporter("tester1")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(0, issues.length);
    }

    @Test
    @DisplayName("get issues title: beta, reporter: tester2") // 4
    void getIssueTitleBetaReporterTester2() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .title("beta")
                .reporter("tester2")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues title: zeta, reporter: tester2") // 3
    void getIssueTitleZetaReporterTester2() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .title("zeta")
                .reporter("tester2")
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(3, issues.length);
    }

    @Test
    @DisplayName("get issues fixer: dev2, status: closed") // 1
    void getIssueFixerDev2StatusClosed() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .fixer("dev2")
                .status(IssueStatus.CLOSED)
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(1, issues.length);
    }

    @Test
    @DisplayName("get issues assignee: dev3, priority: low") // 1
    void getIssueAssigneeDev3PriorityLow() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .assignee("dev3")
                .priority(IssuePriority.LOW)
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "dev1")
                        .param("password", "dev1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(1, issues.length);
    }

    @Test
    @DisplayName("get issues title: zeta2, reporter: tester2, fixer: null, assignee: dev3, priority: critical, status: assigned") // 1
    void getIssueFinalBoss() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .title("zeta2")
                .reporter("tester2")
                .fixer("")
                .assignee("dev3")
                .priority(IssuePriority.CRITICAL)
                .status(IssueStatus.ASSIGNED)
                .build();

        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(1, issues.length);
    }

    @Test
    @DisplayName("get issues with wrong permission")
    void getIssueWithWrongPermission() throws Exception {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .build();

        mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "foreign")
                        .param("password", "foreign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issuesGetRequest)))
                .andExpect(status().isForbidden());
    }
}
