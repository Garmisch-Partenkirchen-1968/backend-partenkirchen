package com.example.demo.user;

import com.example.demo.dto.Permission.PermissionPostRequest;
import com.example.demo.dto.project.ProjectPostRequest;
import com.example.demo.dto.user.Fixer;
import com.example.demo.dto.user.UserSignupRequest;
import com.example.demo.entity.Issue;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.entity.enumerate.IssueStatus;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PermissionService;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.antlr.v4.runtime.misc.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
@AutoConfigureRestDocs
public class UserRecommendTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PermissionService permissionService;

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
        addIssue("gamma4", "tester1", "dev1", "dev1", IssuePriority.LOW, IssueStatus.CLOSED);
    }

    @Test
    @DisplayName("get recommended fixers - low") // 8
    void getRecommendedFixersLOW() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/recommend-assignee")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("priority", String.valueOf(IssuePriority.LOW)))
                .andExpect(status().isOk())
                .andReturn();

        Fixer[] fixer = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Fixer[].class);
        assertEquals(3, fixer.length);

        assertEquals("dev1", fixer[0].getUsername());
        assertEquals(IssuePriority.LOW, fixer[0].getPriority());
        assertEquals(2, fixer[0].getNumberOfFixed());

        assertEquals("dev2", fixer[1].getUsername());
        assertEquals(IssuePriority.LOW, fixer[1].getPriority());
        assertEquals(1, fixer[1].getNumberOfFixed());

        assertEquals("dev3", fixer[2].getUsername());
        assertEquals(IssuePriority.LOW, fixer[2].getPriority());
        assertEquals(1, fixer[2].getNumberOfFixed());
    }

    @Test
    @DisplayName("get recommended fixers - medium") // 8
    void getRecommendedFixersMIDEUM() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/recommend-assignee")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("priority", String.valueOf(IssuePriority.MEDIUM)))
                .andExpect(status().isOk())
                .andReturn();

        Fixer[] fixer = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Fixer[].class);
        assertEquals(3, fixer.length);

        assertEquals("dev2", fixer[0].getUsername());
        assertEquals(IssuePriority.MEDIUM, fixer[0].getPriority());
        assertEquals(1, fixer[0].getNumberOfFixed());

        assertEquals("dev3", fixer[1].getUsername());
        assertEquals(IssuePriority.MEDIUM, fixer[1].getPriority());
        assertEquals(1, fixer[1].getNumberOfFixed());

        assertEquals("dev4", fixer[2].getUsername());
        assertEquals(IssuePriority.MEDIUM, fixer[2].getPriority());
        assertEquals(1, fixer[2].getNumberOfFixed());
    }

    @Test
    @DisplayName("get recommended fixers - high") // 8
    void getRecommendedFixersHIGH() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/recommend-assignee")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("priority", String.valueOf(IssuePriority.HIGH)))
                .andExpect(status().isOk())
                .andReturn();

        Fixer[] fixer = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Fixer[].class);
        assertEquals(3, fixer.length);

        assertEquals("dev1", fixer[0].getUsername());
        assertEquals(IssuePriority.HIGH, fixer[0].getPriority());
        assertEquals(1, fixer[0].getNumberOfFixed());

        assertEquals("dev3", fixer[1].getUsername());
        assertEquals(IssuePriority.HIGH, fixer[1].getPriority());
        assertEquals(1, fixer[1].getNumberOfFixed());

        assertEquals("dev4", fixer[2].getUsername());
        assertEquals(IssuePriority.HIGH, fixer[2].getPriority());
        assertEquals(1, fixer[2].getNumberOfFixed());
    }

    @Test
    @DisplayName("get recommended fixers - critical") // 8
    void getRecommendedFixersCRITICAL() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/projects/" + projectId + "/recommend-assignee")
                        .param("username", "admin")
                        .param("password", "admin")
                        .param("priority", String.valueOf(IssuePriority.CRITICAL)))
                .andExpect(status().isOk())
                .andReturn();

        Fixer[] fixer = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Fixer[].class);
        assertEquals(3, fixer.length);

        assertEquals("dev1", fixer[0].getUsername());
        assertEquals(IssuePriority.CRITICAL, fixer[0].getPriority());
        assertEquals(1, fixer[0].getNumberOfFixed());

        assertEquals("dev2", fixer[1].getUsername());
        assertEquals(IssuePriority.CRITICAL, fixer[1].getPriority());
        assertEquals(1, fixer[1].getNumberOfFixed());

        assertEquals("dev3", fixer[2].getUsername());
        assertEquals(IssuePriority.LOW, fixer[2].getPriority());
        assertEquals(1, fixer[2].getNumberOfFixed());
    }
}
