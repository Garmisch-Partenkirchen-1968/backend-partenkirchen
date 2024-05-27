package com.example.demo.controller;

import com.example.demo.dto.issue.*;
import com.example.demo.entity.Issue;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.entity.enumerate.IssueStatus;
import com.example.demo.service.IssueService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class IssueController {
    private final IssueService issueService;
    private final UserService userService;

    @PostMapping("/projects/{projectId}/issues")
    public ResponseEntity<IssuePostResponse> postIssue(@PathVariable("projectId") Long projectId, @RequestBody IssuePostRequest issuePostRequest) {
        userService.RequesterIsFound(issuePostRequest);
        return new ResponseEntity<> (issueService.postIssue(projectId, issuePostRequest), HttpStatus.CREATED);
    }

    // 파라미터로 추가해야됨
    @GetMapping("/projects/{projectId}/issues")
    public ResponseEntity<List<Issue>> getIssues(@PathVariable("projectId") Long projectId,
                                                 @RequestParam(value = "username", defaultValue = "") String username,
                                                 @RequestParam(value = "password", defaultValue = "") String password,
                                                 @RequestParam(required = false, value = "title") String title,
                                                 @RequestParam(required = false, value = "reporter") String reporter,
                                                 @RequestParam(required = false, value = "fixer") String fixer,
                                                 @RequestParam(required = false, value = "assignee") String assignee,
                                                 @RequestParam(required = false, value = "priority") IssuePriority priority,
                                                 @RequestParam(required = false, value = "status") IssueStatus status,
                                                 @RequestParam(required = false, value = "reportedDate") LocalDateTime reportedDate) {
        IssuesGetRequest issuesGetRequest = IssuesGetRequest.builder()
                .username(username)
                .password(password)
                .title(title)
                .reporter(reporter)
                .fixer(fixer)
                .assignee(assignee)
                .priority(priority)
                .status(status)
                .reportedDate(reportedDate)
                .build();

        userService.RequesterIsFound(issuesGetRequest);
        return new ResponseEntity<>(issueService.getIssues(projectId, issuesGetRequest), HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity<Issue> getIssue(@PathVariable("projectId") Long projectId,
                                          @PathVariable("issueId") Long issueId,
                                          @RequestParam(value = "username", defaultValue = "") String username,
                                          @RequestParam(value = "password", defaultValue = "") String password) {

        IssueGetRequest issueGetRequest = new IssueGetRequest();
        issueGetRequest.setUsername(username);
        issueGetRequest.setPassword(password);
        userService.RequesterIsFound(issueGetRequest);
        return new ResponseEntity<>(issueService.getIssue(projectId, issueId, issueGetRequest), HttpStatus.OK);
    }

    @PatchMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity patchIssue(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody IssuePatchRequest issuePatchRequest) {
        userService.RequesterIsFound(issuePatchRequest);
        issueService.patchIssue(projectId, issueId, issuePatchRequest);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity deleteIssue(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody IssueDeleteRequest issueDeleteRequest) {
        userService.RequesterIsFound(issueDeleteRequest);
        issueService.deleteIssue(projectId, issueId, issueDeleteRequest);
        return new ResponseEntity(HttpStatus.OK);
    }
}
