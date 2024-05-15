package com.example.demo.service;

import com.example.demo.dto.issue.*;
import com.example.demo.entity.Issue;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.entity.enumerate.IssueStatus;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Triple;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssueService {
    IssueRepository issueRepository;
    ProjectRepository projectRepository;
    UserRepository userRepository;

    public ResponseEntity<IssuePostResponse> postIssue(Long projectId, IssuePostRequest issuePostRequest) {
        User user = issuePostRequest.toUser();
        Optional<Project> proj = projectRepository.findById(projectId);

        if (proj.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project not found");
        }
        Project project = proj.get();

        if ((project.getMembers().get(user) & (1 << 1)) == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not tester");
        }

        Issue issue = new Issue();
        issue.setPriority(issuePostRequest.getPriority());
        issue.setTitle(issuePostRequest.getTitle());
        issue.setReporter(user);
        issue.setReportedDate(LocalDateTime.now());
        issueRepository.save(issue);

        IssuePostResponse issuePostResponse = new IssuePostResponse(issue.getId(), issue.getTitle(), issue.getPriority());
        return new ResponseEntity<>(issuePostResponse, HttpStatus.CREATED);
    }

    public ResponseEntity<List<Issue>> getIssues(Long projectId, IssuesGetRequest issuesGetRequest) {
        Optional<Project> proj = projectRepository.findById(projectId);
        if (proj.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project not found");
        }

        Project project = proj.get();
        User user = issuesGetRequest.toUser();
        if (project.getMembers().get(user) == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not member of project");
        }

        List<Issue> issues = project.getIssues();

        // Title이 요청으로 왔을 때
        if (issuesGetRequest.getTitle() != null) {
            issues.removeIf(issue -> !(issuesGetRequest.getTitle().equals(issue.getTitle())));
        }
        // Reporter가 요청으로 왔을 때
        if (issuesGetRequest.getReporter() != null) {
            Optional<User> report = userRepository.findByUsername(issuesGetRequest.getReporter());
            // 검색한 reporter가 존재하는지
            if (report.isPresent()) {
                User reporter = report.get();
                issues.removeIf(issue -> !(issue.getReporter().equals(reporter)));
            } else {
                issues.clear();
            }
        }
        // ReportedDate가 요청으로 왔을 때
        if (issuesGetRequest.getReportedDate() != null) {
            issues.removeIf(issue -> !(issue.getReportedDate().isEqual(issuesGetRequest.getReportedDate())));
        }
        // Fixer가 요청으로 왔을 때
        if (issuesGetRequest.getFixer() != null) {
            Optional<User> fix = userRepository.findByUsername(issuesGetRequest.getFixer());
            if (issuesGetRequest.getFixer().equals("")) { // fixer 배정 안 된 issue 찾기
                issues.removeIf(issue -> issue.getFixer() != null);
            } else {
                // fixer가 존재하는지
                if (fix.isPresent()) {
                    User fixer = fix.get();
                    issues.removeIf(issue -> !(issue.getFixer().equals(fixer)));
                } else {
                    issues.clear();
                }
            }
        }
        // Asignee가 요청으로 왔을 때
        if (issuesGetRequest.getAssignee() != null) {
            Optional<User> assign = userRepository.findByUsername(issuesGetRequest.getAssignee());
            if (issuesGetRequest.getAssignee().equals("")) { // assignee 배정 안 된 issue 찾기
                issues.removeIf(issue -> issue.getAssignee() != null);
            } else {
                // assignee가 존재하는지
                if (assign.isPresent()) {
                    User assignee = assign.get();
                    issues.removeIf(issue -> !(issue.getAssignee().equals(assignee)));
                } else {
                    issues.clear();
                }
            }

        }
        // Priority가 요청으로 왔을 때
        if (issuesGetRequest.getPriority() != null) {
            issues.removeIf(issue -> !(issue.getPriority().equals(issuesGetRequest.getPriority())));
        }
        // Status가 요청으로 왔을 때
        if (issuesGetRequest.getStatus() != null) {
            issues.removeIf(issue -> !(issue.getStatus().equals(issuesGetRequest.getStatus())));
        }

        return new ResponseEntity<>(issues, HttpStatus.OK);
    }

    public ResponseEntity<Issue> getIssue(Long projectId, Long issueId, IssueGetRequest issueGetRequest) {
        Pair<Project, Issue> PI = FindPI(projectId, issueId);

        Project project = PI.a;
        Issue issue = PI.b;
        User user = issueGetRequest.toUser();

        if (project.getMembers().get(user) == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not tester");
        }

        return new ResponseEntity<>(issue, HttpStatus.OK);
    }

    public ResponseEntity patchIssue(Long projectId, Long issueId, IssuePatchRequest issuePatchRequest) {
        Pair<Project, Issue> PI = FindPI(projectId, issueId);

        Optional<User> assign = userRepository.findByUsername(issuePatchRequest.getAssignee());
        if (assign.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not exists");
        }

        Project project = PI.a;
        Issue issue = PI.b;
        User user = issuePatchRequest.toUser();
        User assignee = assign.get();

        issue.setPriority(issuePatchRequest.getPriority());

        Integer userPermission = project.getMembers().get(user);
        // Assignee 수정(PL만 가능)
        if (issue.getAssignee() != assignee) {
            if ((userPermission & (1 << 3)) == 0) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not tester");
            }
            issue.setAssignee(assignee);
        }
        // Status FIXED로 수정(assignee만 가능)
        else if (issuePatchRequest.getStatus() == IssueStatus.FIXED) {
            if (issue.getAssignee() != user) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not tester");
            }
            issue.setStatus(IssueStatus.FIXED);
        }
        // Status RESOLVED로 수정(reporter만 가능)
        else if (issuePatchRequest.getStatus() == IssueStatus.RESOLVED) {
            if (issue.getReporter() != user) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not reporter");
            }
            issue.setStatus(IssueStatus.RESOLVED);
        }
        // Status closed로 바꿈(PL만 가능)
        else if (issuePatchRequest.getStatus() == IssueStatus.CLOSED) {
            if ((userPermission & (1 << 3)) == 0) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not PL");
            }
        }

        return new ResponseEntity<>(issueRepository.save(issue), HttpStatus.OK);

    }

    public ResponseEntity deleteIssue(Long projectId, Long issueId, IssueDeleteRequest issueDeleteRequest) {
        Pair<Project, Issue> PI = FindPI(projectId, issueId);

        Issue issue = PI.b;
        User user = issueDeleteRequest.toUser();

        if (issue.getReporter() != user) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not reporter");
        }

        issueRepository.delete(issue);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public Pair<Project, Issue> FindPI(Long projectId, Long issueId) {
        Optional<Project> project = projectRepository.findById(projectId);
        Optional<Issue> issue = issueRepository.findById(issueId);

        if (project.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Project not found");
        }
        if (issue.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Issue not found");
        }

        return new Pair(project.get(), issue.get());
    }
}