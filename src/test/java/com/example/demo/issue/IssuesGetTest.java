package com.example.demo.issue;

import com.example.demo.dto.project.PermissionRequest;
import com.example.demo.dto.project.ProjectCreater;
import com.example.demo.entity.Issue;
import com.example.demo.entity.User;
import com.example.demo.repository.IssueRepository;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

    private Long projectId;

    // admin, PL1, tester1, dev1, foreign
    @BeforeEach
    void init() throws Exception {
        // create admin
        User admin = User.builder()
                .username("admin")
                .password("admin")
                .build();
        userService.signUpUser(admin);

        // create PL1
        User PL1 = User.builder()
                .username("tester1")
                .password("tester1")
                .build();
        Long PL1Id = userService.signUpUser(PL1).getId();

        // create tester1
        User tester1 = User.builder()
                .username("tester1")
                .password("tester1")
                .build();
        Long tester1Id = userService.signUpUser(tester1).getId();

        // create tester1
        User dev1 = User.builder()
                .username("dev1")
                .password("dev1")
                .build();
        Long dev1Id = userService.signUpUser(dev1).getId();

        // create foreign
        User foreign = User.builder()
                .username("foreign")
                .password("foreign")
                .build();
        userService.signUpUser(foreign);

        // create project
        ProjectCreater projectCreater = ProjectCreater.builder()
                .username("admin")
                .password("admin")
                .projectName("new project!")
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

        // give tester permission to tester1
        PermissionRequest dev1PermissionRequest = PermissionRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, false, true})
                .build();
        projectService.addPermission(projectId, dev1Id, dev1PermissionRequest);
    }
}
