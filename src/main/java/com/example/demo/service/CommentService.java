package com.example.demo.service;

import com.example.demo.controller.CommentController;
import com.example.demo.dto.comment.CommentDeleteRequest;
import com.example.demo.dto.comment.CommentPatchRequest;
import com.example.demo.dto.comment.CommentPostRequest;
import com.example.demo.dto.comment.CommentPostResponse;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Issue;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class CommentService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;

    public CommentService(ProjectRepository projectRepository, UserRepository userRepository, IssueRepository issueRepository, CommentRepository commentRepository, CommentController commentController) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.issueRepository = issueRepository;
        this.commentRepository = commentRepository;
    }

    public CommentPostResponse postComment(Long projectId, Long issueId, Long userId, CommentPostRequest commentPostRequest) {
        Project project = getProject(projectId);
        Issue issue = getIssue(issueId);
        User user = getUser(userId);

        return null;
    }

    public void patchComment(Long projectId, Long issueId, Long commentId, Long userId, CommentPatchRequest commentPatchRequest) {
        Project project = getProject(projectId);
        Issue issue = getIssue(issueId);
        User user = getUser(userId);
        Comment comment = getComment(commentId);

    }

    public void deleteComment(Long projectId, Long issueId, Long commentId, Long userId, CommentDeleteRequest commentDeleteRequest) {
        Project project = getProject(projectId);
        Issue issue = getIssue(issueId);
        User user = getUser(userId);
        Comment comment = getComment(commentId);

    }

    private Project getProject(Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project not found");
        }
        return optionalProject.get();
    }

    private Issue getIssue(Long issueId) {
        Optional<Issue> optionalIssue = issueRepository.findById(issueId);
        if (optionalIssue.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "issue not found");
        }
        return optionalIssue.get();
    }

    private User getUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found");
        }
        return optionalUser.get();
    }

    private Comment getComment(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "comment not found");
        }
        return optionalComment.get();
    }
}
