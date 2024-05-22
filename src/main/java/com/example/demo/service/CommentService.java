package com.example.demo.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

@Service
public class CommentService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(ProjectRepository projectRepository, UserRepository userRepository, IssueRepository issueRepository, CommentRepository commentRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.issueRepository = issueRepository;
        this.commentRepository = commentRepository;
    }

    public CommentPostResponse postComment(Long projectId, Long issueId, Long userId, CommentPostRequest commentPostRequest) {
        Project project = getProject(projectId);
        Issue issue = getIssue(issueId);
        User user = getUser(userId);

        // 요청한 사람이 해당 프로젝트에 있는 사람인지 권한 확인
        if (project.getMembers().get(user) == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "request is not in this project");
        }

        if (commentPostRequest.getContent() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content is required");
        }

        if (commentPostRequest.getIsDescription() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is required");
        }

        Comment comment = new Comment(commentPostRequest.getContent(), user, commentPostRequest.getIsDescription());
        comment = commentRepository.save(comment);
        issue.getComments().add(comment);
        issueRepository.save(issue);

        return new CommentPostResponse(comment.getId());
    }

    public void patchComment(Long projectId, Long commentId, Long userId, CommentPatchRequest commentPatchRequest) {
        Project project = getProject(projectId);
        User user = getUser(userId);
        Comment comment = getComment(commentId);

        // 요청한 사람이 해당 프로젝트에 있는 사람인지 권한 확인
        if (project.getMembers().get(user) == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "request is not in this project");
        }

        // 요청을 한 사람이 그 comment를 작성한 사람인지 확인
        if (Objects.equals(comment.getCommenter().getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "request is not in this project");
        }

        if (commentPatchRequest.getContent() != null) {
            comment.setContent(comment.getContent());
        }
        if (commentPatchRequest.getIsDescription() != null) {
            comment.setIsDescription(commentPatchRequest.getIsDescription());
        }

        commentRepository.save(comment);
    }

    public void deleteComment(Long projectId, Long commentId, Long userId) {
        Project project = getProject(projectId);
        User user = getUser(userId);
        Comment comment = getComment(commentId);

        // 요청한 사람이 해당 프로젝트에 있는 사람인지 권한 확인
        if (project.getMembers().get(user) == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "requester is not in this project");
        }

        // 요청을 한 사람이 그 comment를 작성한 사람인지 확인
        if (!Objects.equals(comment.getCommenter().getId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "requester is not writer");
        }

        commentRepository.delete(comment);
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
