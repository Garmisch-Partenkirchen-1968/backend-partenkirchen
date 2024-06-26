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
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public IssueService(IssueRepository issueRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public IssuePostResponse postIssue(Long projectId, IssuePostRequest issuePostRequest) {
        User user = getUserByUsername(issuePostRequest.toUser().getUsername());
        Optional<Project> proj = projectRepository.findById(projectId);
        System.out.println("PostIssue");

        // project가 없는 경우
        if (proj.isEmpty()) {
            System.out.println("Project not found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project not found");
        }
        Project project = proj.get();

        // user가 project member가 아닌 경우
        if(project.getMembers().get(user) == null){
            System.out.println("User is not a member of this project");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a member of this project");
        }

        // user가 tester가 아닌 경우
        if ((project.getMembers().get(user) & (1 << 1)) == 0) {
            System.out.println("User is not tester");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not tester");
        }

        // title 입력이 없는 경우
        if(issuePostRequest.getTitle().isEmpty()){
            System.out.println("Title is required");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }

        Issue issue = new Issue();
        issue.setPriority(issuePostRequest.getPriority());
        issue.setTitle(issuePostRequest.getTitle());
        issue.setReporter(user);
        issue.setReportedDate(LocalDateTime.now());
        issue.setStatus(IssueStatus.NEW);
        issueRepository.save(issue);
        project.getIssues().add(issue);
        projectRepository.save(project);

        return new IssuePostResponse(issue.getId(), issue.getTitle(), issue.getPriority());
    }

    public List<Issue> getIssues(Long projectId, IssuesGetRequest issuesGetRequest) {
        Optional<Project> proj = projectRepository.findById(projectId);
        // project가 없는 경우
        if (proj.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project not found");
        }

        Project project = proj.get();
        User user = getUserByUsername(issuesGetRequest.toUser().getUsername());

        // user가 project의 member가 아닐 때
        if (project.getMembers().get(user) == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not member of project");
        }

        List<Issue> issues = project.getIssues();

        // Title이 요청으로 왔을 때
        if (issuesGetRequest.getTitle() != null) {
            issues.removeIf(issue -> !(issue.getTitle().contains(issuesGetRequest.getTitle())));
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
            if (issuesGetRequest.getFixer().isEmpty()) { // fixer 배정 안 된 issue 찾기
                issues.removeIf(issue -> issue.getFixer() != null);
            }
            else {
                // fixer가 존재하는지
                if (fix.isPresent()) {
                    User fixer = fix.get();
                    issues.removeIf(issue -> !(fixer.equals(issue.getFixer())));
                } else {
                    issues.clear();
                }
            }
        }
        // Asignee가 요청으로 왔을 때
        if (issuesGetRequest.getAssignee() != null) {
            Optional<User> assign = userRepository.findByUsername(issuesGetRequest.getAssignee());
            if (issuesGetRequest.getAssignee().isEmpty()) { // assignee 배정 안 된 issue 찾기
                issues.removeIf(issue -> issue.getAssignee() != null);
            } else {
                // assignee가 존재하는지
                if (assign.isPresent()) {
                    User assignee = assign.get();
                    issues.removeIf(issue -> !(assignee.equals(issue.getAssignee())));
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

        return issues;
    }

    public Issue getIssue(Long projectId, Long issueId, IssueGetRequest issueGetRequest) {
        Pair<Project, Issue> PI = FindPI(projectId, issueId);

        Project project = PI.a;
        Issue issue = PI.b;
        User user = getUserByUsername(issueGetRequest.getUsername());

        if (project.getMembers().get(user) == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not tester");
        }

        return issue;
    }

    public void patchIssue(Long projectId, Long issueId, IssuePatchRequest issuePatchRequest) {
        Pair<Project, Issue> PI = FindPI(projectId, issueId);
        Project project = PI.a;
        Issue issue = PI.b;
        Optional<User> assign = userRepository.findByUsername(issuePatchRequest.getAssignee());

        User user = getUserByUsername(issuePatchRequest.toUser().getUsername());

        // user가 project 소속 인원이 아닐 때
        if(project.getMembers().get(user) == null) {
            System.out.println("User is not member of project");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not member of project");
        }

        Integer userPermission = project.getMembers().get(user);

        // title 받았을 때
        if(issuePatchRequest.getTitle() != null){
            if(issue.getReporter() != user){
                System.out.println("User is not reporter");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not reporter");
            }
            issue.setTitle(issuePatchRequest.getTitle());
        }

        // 없는 사람을 asignee로 요청
        if(issuePatchRequest.getAssignee() != null && assign.isEmpty()){
            System.out.println("Assignee is not in project");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee is not in project");
        }

        // priority 받았을 때
        if(issuePatchRequest.getPriority() != null){
            issue.setPriority(issuePatchRequest.getPriority());
        }

        // assignee 받았을 때
        if(assign.isPresent()){
            User assignee = assign.get();
            // Assignee 수정, 원래랑 다른 입력을 받을 때만 변경
            if (issue.getAssignee() == null || issue.getAssignee() != assignee) {
                // permission check(PL만 가능)
                if ((userPermission & (1 << 2)) == 0) {
                    System.out.println("User is not PL");
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not PL");
                }
                if((project.getMembers().get(assignee) & (1 << 0)) == 0) {
                    System.out.println("Assignee is not developer");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee is not developer");
                }
                issue.setAssignee(assignee);
                issue.setStatus(IssueStatus.ASSIGNED);
            }
        }

        // Status FIXED로 수정(assignee만 가능)
        else if (issuePatchRequest.getStatus() == IssueStatus.FIXED) {
            if (issue.getAssignee() != user) {
                System.out.println("User is not assignee");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not assignee");
            }
            issue.setFixer(user);
            issue.setStatus(IssueStatus.FIXED);
        }
        // Status RESOLVED로 수정(reporter만 가능)
        else if (issuePatchRequest.getStatus() == IssueStatus.RESOLVED) {
            if (issue.getReporter() != user) {
                System.out.println("User is not reporter");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not reporter");
            }
            issue.setStatus(IssueStatus.RESOLVED);
        }
        // Status closed로 바꿈(PL만 가능)
        else if (issuePatchRequest.getStatus() == IssueStatus.CLOSED) {
            if ((userPermission & (1 << 2)) == 0) {
                System.out.println("User is not PL");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not PL");
            }
            issue.setStatus(IssueStatus.CLOSED);
        }
        // Status reopened로 바꿈(PL만 가능)
        else if (issuePatchRequest.getStatus() == IssueStatus.REOPENED) {
            if ((userPermission & (1 << 2)) == 0) {
                System.out.println("User is not PL");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not PL");
            }
            issue.setStatus(IssueStatus.REOPENED);
        }

        issueRepository.save(issue);
    }

    public void deleteIssue(Long projectId, Long issueId, IssueDeleteRequest issueDeleteRequest) {
        Pair<Project, Issue> PI = FindPI(projectId, issueId);

        Project project = PI.a;
        Issue issue = PI.b;

        User user = getUserByUsername(issueDeleteRequest.toUser().getUsername());

        // User가 Reporter가 아니면
        if (!Objects.equals(issue.getReporter().getId(), user.getId())) {
            System.out.println("User is not reporter");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not reporter");
        }

        project.getIssues().remove(issue);
        issueRepository.delete(issue);
        projectRepository.save(project);
    }

    public Pair<Project, Issue> FindPI(Long projectId, Long issueId) {
        Optional<Project> project = projectRepository.findById(projectId);
        Optional<Issue> issue = issueRepository.findById(issueId);

        if (project.isEmpty()) {
            System.out.println("Project not found");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Project not found");
        }
        if (issue.isEmpty()) {
            System.out.println("Issue not found");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Issue not found");
        }

        return new Pair<>(project.get(), issue.get());
    }

    private User getUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found");
        }
        return optionalUser.get();
    }
}