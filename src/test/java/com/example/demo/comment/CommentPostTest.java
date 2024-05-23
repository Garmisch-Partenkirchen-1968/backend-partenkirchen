package com.example.demo.comment;

import com.example.demo.dto.comment.CommentPostRequest;
import com.example.demo.dto.comment.CommentPostResponse;
import com.example.demo.dto.issue.IssuePostRequest;
import com.example.demo.dto.issue.IssuePostResponse;
import com.example.demo.dto.project.PermissionRequest;
import com.example.demo.dto.project.ProjectPostRequest;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Issue;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
public class CommentPostTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    Long projectId;
    Issue defaultIssue;
    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void init() throws Exception {
        // admin 생성
        User admin = User.builder()
                .username("admin")
                .password("admin")
                .build();
        userService.signUpUser(admin);

        // issue를 추가할 tester 생성
        User tester = User.builder()
                .username("tester")
                .password("tester")
                .build();
        Long testerId = userService.signUpUser(tester).getId();

        // comment를 추가할 dev(dev1) 생성
        User dev1 = User.builder()
                .username("dev1")
                .password("dev1")
                .build();
        Long dev1Id = userService.signUpUser(dev1).getId();

        // project에 해당되지 않는 dev(dev2) 생성
        User dev2 = User.builder()
                .username("dev2")
                .password("dev2")
                .build();
        Long dev2Id = userService.signUpUser(dev2).getId();

        // project 생성
        ProjectPostRequest projectCreater = ProjectPostRequest.builder()
                .username("admin")
                .password("admin")
                .name("TestProject")
                .description("some description")
                .build();
        projectId = projectService.createProject(projectCreater).getId();

        // project에 tester 할당
        PermissionRequest testerPermissionRequest = PermissionRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, true, false})
                .build();
        projectService.addPermission(projectId, testerId, testerPermissionRequest);

        // project에 dev1 할당
        PermissionRequest dev1PermissionRequest = PermissionRequest.builder()
                .username("admin")
                .password("admin")
                .permissions(new boolean[] {false, false, false, true})
                .build();
        projectService.addPermission(projectId, dev1Id, dev1PermissionRequest);

        // issue 생성 (reporter는 tester)
        IssuePostRequest issuePostRequest = IssuePostRequest.builder()
                .username("tester")
                .password("tester")
                .title("TestIssue")
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
        defaultIssue = optionalIssue.get();
    }

    @Test
    @DisplayName("members의 description comment 등록 성공")
    void postComment() throws Exception {
        // 1. tester(reporter)의 description
        CommentPostRequest commentTruePostRequest = CommentPostRequest.builder()
                .username("tester")
                .password("tester")
                .content("This is initial description")
                .isDescription(true)
                .build();
        MvcResult mvcTrueResult = this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentTruePostRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        // comment 잘 등록됐는지 확인
        CommentPostResponse commentTruePostResponse = objectMapper.readValue(mvcTrueResult.getResponse().getContentAsString(), CommentPostResponse.class);
        Optional<Comment> optionalTrueComment = commentRepository.findById(commentTruePostResponse.getCommentId());
        assertTrue(optionalTrueComment.isPresent());

        // comment component 확인
        Comment Truecomment = optionalTrueComment.get();
        assertEquals(Truecomment.getId(), commentTruePostResponse.getCommentId());
        assertEquals(Truecomment.getCommenter(), userRepository.findByUsername("tester").get());
        assertEquals(Truecomment.getContent(), "This is initial description");
        assertEquals(Truecomment.getIsDescription(), true);

        // 2. tester(reporter)의 comment
        CommentPostRequest commentFalsePostRequest = CommentPostRequest.builder()
                .username("tester")
                .password("tester")
                .content("This is initial comment")
                .isDescription(false)
                .build();
        MvcResult mvcFalseResult = this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentFalsePostRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        // comment 잘 등록됐는지 확인
        CommentPostResponse commentFalsePostResponse = objectMapper.readValue(mvcFalseResult.getResponse().getContentAsString(), CommentPostResponse.class);
        Optional<Comment> optionalFalseComment = commentRepository.findById(commentFalsePostResponse.getCommentId());
        assertTrue(optionalFalseComment.isPresent());

        // comment component 확인
        Comment Falsecomment = optionalFalseComment.get();
        assertEquals(Falsecomment.getId(), commentFalsePostResponse.getCommentId());
        assertEquals(Falsecomment.getCommenter(), userRepository.findByUsername("tester").get());
        assertEquals(Falsecomment.getContent(), "This is initial comment");
        assertEquals(Falsecomment.getIsDescription(), false);

        // 3. dev1의 comment
        CommentPostRequest dev1commentPostRequest = CommentPostRequest.builder()
                .username("dev1")
                .password("dev1")
                .content("This is dev1's description")
                .isDescription(false)
                .build();
        MvcResult dev1mvcResult = this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev1commentPostRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        // comment 잘 등록됐는지 확인
        CommentPostResponse dev1commentPostResponse = objectMapper.readValue(dev1mvcResult.getResponse().getContentAsString(), CommentPostResponse.class);
        Optional<Comment> dev1optionalComment = commentRepository.findById(dev1commentPostResponse.getCommentId());
        assertTrue(dev1optionalComment.isPresent());

        // comment component 확인
        Comment dev1comment = dev1optionalComment.get();
        assertEquals(dev1comment.getId(), dev1commentPostResponse.getCommentId());
        assertEquals(dev1comment.getCommenter(), userRepository.findByUsername("dev1").get());
        assertEquals(dev1comment.getContent(), "This is dev1's description");
        assertEquals(dev1comment.getIsDescription(), false);
    }

    @Test
    @DisplayName("존재하지 않는 user의 comment 등록")
    void goastPostComment() throws Exception {
        CommentPostRequest commentPostRequest = CommentPostRequest.builder()
                .username("goast")
                .password("goast")
                .content("I'm goast")
                .isDescription(false)
                .build();
        this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(commentPostRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("project에 없는 user의 comment 등록")
    void nonMemberPostComment() throws Exception {
        CommentPostRequest commentPostRequest = CommentPostRequest.builder()
                .username("dev2")
                .password("dev2")
                .content("I'm dev2")
                .isDescription(false)
                .build();
        this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentPostRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("미등록자의 Description comment 등록")
    void nonRegisterPostComment() throws Exception {
        CommentPostRequest commentPostRequest = CommentPostRequest.builder()
                .username("dev2")
                .password("dev2")
                .content("This is wrong description")
                .isDescription(true)
                .build();
        // 미등록자의 description은 Forbidden
        this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentPostRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("content가 없는 경우")
    void nonContentPostComment() throws Exception {
        CommentPostRequest commentPostRequest = CommentPostRequest.builder()
                .username("dev1")
                .password("dev1")
                .isDescription(true)
                .build();
        // content 없으면 bad request
        this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentPostRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Description이 없는 경우")
    void nonDescriptionPostComment() throws Exception {
        CommentPostRequest commentPostRequest = CommentPostRequest.builder()
                .username("dev1")
                .password("dev1")
                .content("This is wrong description")
                .build();
        // Description 없으면 bad request
        this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentPostRequest)))
                .andExpect(status().isBadRequest());
    }
}
