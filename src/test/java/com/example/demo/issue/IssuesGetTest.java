package com.example.demo.issue;

import com.example.demo.dto.Permission.PermissionPostRequest;
import com.example.demo.dto.issue.IssuesGetRequest;
import com.example.demo.dto.project.ProjectPostRequest;
import com.example.demo.dto.user.UserSignupRequest;
import com.example.demo.entity.Issue;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.entity.enumerate.IssueStatus;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IssueService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs
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
    private PermissionService permissionService;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IssueService issueService;

    private Long projectId;

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
        UserSignupRequest admin = UserSignupRequest.builder()
                .username("admin")
                .password("admin")
                .build();
        userService.signUpUser(admin);

        // create PL1
        UserSignupRequest PL1 = UserSignupRequest.builder()
                .username("PL1")
                .password("PL1")
                .build();
        Long PL1Id = userService.signUpUser(PL1).getId();

        // create tester1
        UserSignupRequest tester1 = UserSignupRequest.builder()
                .username("tester1")
                .password("tester1")
                .build();
        Long tester1Id = userService.signUpUser(tester1).getId();

        // create tester2
        UserSignupRequest tester2 = UserSignupRequest.builder()
                .username("tester2")
                .password("tester2")
                .build();
        Long tester2Id = userService.signUpUser(tester2).getId();

        // create dev1
        UserSignupRequest dev1 = UserSignupRequest.builder()
                .username("dev1")
                .password("dev1")
                .build();
        Long dev1Id = userService.signUpUser(dev1).getId();

        // create dev2
        UserSignupRequest dev2 = UserSignupRequest.builder()
                .username("dev2")
                .password("dev2")
                .build();
        Long dev2Id = userService.signUpUser(dev2).getId();

        // create dev3
        UserSignupRequest dev3 = UserSignupRequest.builder()
                .username("dev3")
                .password("dev3")
                .build();
        Long dev3Id = userService.signUpUser(dev3).getId();

        // create dev4
        UserSignupRequest dev4 = UserSignupRequest.builder()
                .username("dev4")
                .password("dev4")
                .build();
        Long dev4Id = userService.signUpUser(dev4).getId();

        // create foreign
        UserSignupRequest foreign = UserSignupRequest.builder()
                .username("foreign")
                .password("foreign")
                .build();
        userService.signUpUser(foreign);

        // create project
        ProjectPostRequest projectCreater = ProjectPostRequest.builder()
                .username("admin")
                .password("admin")
                .name("new project!")
                .description("some description")
                .build();
        projectId = projectService.createProject(projectCreater).getId();

        // give PL permission to PL1
        PermissionPostRequest PLPermissionRequest = PermissionPostRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, true, false, false})
                .build();
        permissionService.addPermission(projectId, PL1Id, PLPermissionRequest);

        // give tester permission to tester1
        PermissionPostRequest testerPermissionRequest = PermissionPostRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, true, false})
                .build();
        permissionService.addPermission(projectId, tester1Id, testerPermissionRequest);

        // give tester permission to tester2
        permissionService.addPermission(projectId, tester2Id, testerPermissionRequest);

        // give tester permission to dev1
        PermissionPostRequest dev1PermissionRequest = PermissionPostRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, false, true})
                .build();
        permissionService.addPermission(projectId, dev1Id, dev1PermissionRequest);

        // give tester permission to dev2
        permissionService.addPermission(projectId, dev2Id, dev1PermissionRequest);

        // give tester permission to dev2
        permissionService.addPermission(projectId, dev3Id, dev1PermissionRequest);

        // give tester permission to dev2
        permissionService.addPermission(projectId, dev4Id, dev1PermissionRequest);

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
                .andDo(document("issues/gets/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(20, issues.length);
    }

    @Test
    @DisplayName("get issues title: alpha") // 4
    void getIssueTitleAlpha() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("title", "alpha"))
                .andExpect(status().isOk())
                .andDo(document("issues/gets/success-with-issue-title",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues title: delta") // 3
    void getIssueTitleDelta() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("title", "delta"))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(3, issues.length);
    }

    @Test
    @DisplayName("get issues title: 2") // 6
    void getIssueTitle2() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("title", "2"))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(6, issues.length);
    }

    @Test
    @DisplayName("get issues title: 4") // 2
    void getIssueTitle4() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("title", "4"))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(2, issues.length);
    }

    @Test
    @DisplayName("get issues reporter: tester1") // 10
    void getIssueReporterTester1() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("reporter", "tester1"))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(10, issues.length);
    }

    @Test
    @DisplayName("get issues reporter: tester2") // 10
    void getIssueReporterTester2() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("reporter", "tester2"))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(10, issues.length);
    }

    @Test
    @DisplayName("get issues fixer: null") // 8
    void getIssueFixerNull() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("fixer", ""))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(8, issues.length);
    }

    @Test
    @DisplayName("get issues fixer: dev1") // 3
    void getIssueFixerDev1() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("fixer", "dev1"))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(3, issues.length);
    }

    @Test
    @DisplayName("get issues fixer: dev3") // 3
    void getIssueFixerDev3() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("fixer", "dev3"))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(3, issues.length);
    }

    @Test
    @DisplayName("get issues assignee: null") // 4
    void getIssueAssigneeNull() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("assignee", ""))
                .andExpect(status().isOk())
                .andDo(document("issues/gets/success-with-no-assignee",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues assignee: dev2") // 4
    void getIssueAssigneeDev2() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("assignee", "dev2"))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues assignee: dev4") // 4
    void getIssueAssigneeDev4() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("assignee", "dev4"))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues priority: medium") // 5
    void getIssuePriorityMedium() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("priority", String.valueOf(IssuePriority.MEDIUM)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(5, issues.length);
    }

    @Test
    @DisplayName("get issues priority: critical") // 5
    void getIssuePriorityCritical() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("priority", String.valueOf(IssuePriority.CRITICAL)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(5, issues.length);
    }

    @Test
    @DisplayName("get issues status: new") // 4
    void getIssueStatusNew() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("status", String.valueOf(IssueStatus.NEW)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues status: fixed") // 4
    void getIssueStatusFixed() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("status", String.valueOf(IssueStatus.FIXED)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues title: beta, reporter: tester1") // 0
    void getIssueTitleBetaReporterTester1() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("title", "beta")
                        .param("reporter", "tester1"))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(0, issues.length);
    }

    @Test
    @DisplayName("get issues title: beta, reporter: tester2") // 4
    void getIssueTitleBetaReporterTester2() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("title", "beta")
                        .param("reporter", "tester2"))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(4, issues.length);
    }

    @Test
    @DisplayName("get issues title: zeta, reporter: tester2") // 3
    void getIssueTitleZetaReporterTester2() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("title", "zeta")
                        .param("reporter", "tester2"))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(3, issues.length);
    }

    @Test
    @DisplayName("get issues fixer: dev2, status: closed") // 1
    void getIssueFixerDev2StatusClosed() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("fixer", "dev2")
                        .param("status", String.valueOf(IssueStatus.CLOSED)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(1, issues.length);
    }

    @Test
    @DisplayName("get issues assignee: dev3, priority: low") // 1
    void getIssueAssigneeDev3PriorityLow() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "dev1")
                        .param("password", "dev1")
                        .param("assignee", "dev3")
                        .param("priority", String.valueOf(IssuePriority.LOW)))
                .andExpect(status().isOk())
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(1, issues.length);
    }

    @Test
    @DisplayName("get issues title: zeta2, reporter: tester2, fixer: null, assignee: dev3, priority: critical, status: assigned") // 1
    void getIssueFinalBoss() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("title", "zeta2")
                        .param("reporter", "tester2")
                        .param("fixer", "")
                        .param("assignee", "dev3")
                        .param("priority", String.valueOf(IssuePriority.CRITICAL))
                        .param("status", String.valueOf(IssueStatus.ASSIGNED)))
                .andExpect(status().isOk())
                .andDo(document("issues/gets/success-combination",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .andReturn();

        Issue[] issues = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Issue[].class);
        assertEquals(1, issues.length);
    }

    @Test
    @DisplayName("get issues with wrong permission")
    void getIssueWithWrongPermission() throws Exception {
        mockMvc.perform(get("/projects/" + projectId + "/issues")
                        .param("username", "foreign")
                        .param("password", "foreign"))
                .andExpect(status().isForbidden());
    }
}
