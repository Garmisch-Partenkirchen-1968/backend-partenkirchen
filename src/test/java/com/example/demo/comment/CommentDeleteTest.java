package com.example.demo.comment;

import com.example.demo.dto.comment.CommentDeleteRequest;
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
import jakarta.transaction.Status;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@ActiveProfiles
@AutoConfigureMockMvc
@Transactional
public class CommentDeleteTest {
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

    @Autowired
    private CommentRepository commentRepository;

    Long projectId;
    Issue defaultIssue;
    Comment comment1;
    Comment comment2;
    Comment comment3;

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

        // project에 issue 1 생성 (reporter는 tester)
        IssuePostRequest issue1PostRequest = IssuePostRequest.builder()
                .username("tester")
                .password("tester")
                .title("TestIssue")
                .priority(IssuePriority.CRITICAL)
                .build();
        MvcResult mvcIssue1Result = this.mockMvc.perform(post("/projects/" + projectId + "/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issue1PostRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        IssuePostResponse issue1PostResponse = objectMapper.readValue(mvcIssue1Result.getResponse().getContentAsString(), IssuePostResponse.class);
        Optional<Issue> optionalIssue1 = issueRepository.findById(issue1PostResponse.getId());
        assertTrue(optionalIssue1.isPresent());
        defaultIssue = optionalIssue1.get();

        // tester가 project 1에 Comment(description으로) 1 만들기 (status: Created)
        CommentPostRequest comment1PostRequest = CommentPostRequest.builder()
                .username("tester")
                .password("tester")
                .content("Project 1 Description 1")
                .isDescription(true)
                .build();
        MvcResult mvcComment1Result = this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment1PostRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        CommentPostResponse comment1PostResponse = objectMapper.readValue(mvcComment1Result.getResponse().getContentAsString(), CommentPostResponse.class);
        Optional<Comment> optionalComment1 = commentRepository.findById(comment1PostResponse.getCommentId());
        assertTrue(optionalComment1.isPresent());
        comment1 = optionalComment1.get();

        // tester가 project 1에 Comment(comment로) 2 만들기 (status: Created)
        CommentPostRequest comment2PostRequest = CommentPostRequest.builder()
                .username("tester")
                .password("tester")
                .content("Project 1 Comment 2")
                .isDescription(false)
                .build();
        MvcResult mvcComment2Result = this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(comment2PostRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        CommentPostResponse coment2PostResponse = objectMapper.readValue(mvcComment2Result.getResponse().getContentAsString(), CommentPostResponse.class);
        Optional<Comment> optionalComment2 = commentRepository.findById(coment2PostResponse.getCommentId());
        assertTrue(optionalComment2.isPresent());
        comment2 = optionalComment2.get();

        // dev1이 project 1에 Comment(comment로) 3 만들기 (status: Created)
        CommentPostRequest comment3PostRequest = CommentPostRequest.builder()
                .username("dev1")
                .password("dev1")
                .content("Project 1 Comment 3")
                .isDescription(false)
                .build();
        MvcResult mvcComment3Result = this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment3PostRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        CommentPostResponse comment3PostResponse = objectMapper.readValue(mvcComment3Result.getResponse().getContentAsString(), CommentPostResponse.class);
        Optional<Comment> optionalComment3 = commentRepository.findById(comment3PostResponse.getCommentId());
        assertTrue(optionalComment3.isPresent());
        comment3 = optionalComment3.get();
    }

    @Test
    @DisplayName("Comment 1 Delete 성공")
    void deleteComment1() throws Exception {
        // comment1 삭제
        CommentDeleteRequest comment1DeleteRequest = CommentDeleteRequest.builder()
                .username("tester")
                .password("tester")
                .build();
        this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments/" + comment1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment1DeleteRequest)))
                .andExpect(status().isOk());
        assertFalse(defaultIssue.getComments().contains(comment1));
    }

    @Test
    @DisplayName("Comment 2 Delete 성공")
    void deleteComment2() throws Exception {
        // comment2 삭제
        CommentDeleteRequest comment1DeleteRequest = CommentDeleteRequest.builder()
                .username("tester")
                .password("tester")
                .build();
        this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments/" + comment2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment1DeleteRequest)))
                .andExpect(status().isOk());
        assertFalse(defaultIssue.getComments().contains(comment2));
    }

    @Test
    @DisplayName("Comment 3 Delete 성공")
    void deleteComment3() throws Exception {
        // comment3 삭제
        CommentDeleteRequest comment1DeleteRequest = CommentDeleteRequest.builder()
                .username("dev1")
                .password("dev1")
                .build();
        this.mockMvc.perform(post("/projects/" + projectId + "/issues/" + defaultIssue.getId() + "/comments/" + comment3.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment1DeleteRequest)))
                .andExpect(status().isOk());
        assertFalse(defaultIssue.getComments().contains(comment3));
    }

    @Test
    @DisplayName("Project에 없는 사람이 Delete한 경우")
    void deleteCommitNonProjectMember() throws Exception {
        
    }
}
