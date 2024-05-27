package com.example.demo.controller;

import com.example.demo.dto.issue.*;
import com.example.demo.entity.Issue;
import com.example.demo.service.IssueService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/projects/{projectId}/issues")
    public ResponseEntity<List<Issue>> getIssues(@PathVariable("projectId") Long projectId,
                                                 @RequestParam(value = "username", defaultValue = "") String username,
                                                 @RequestParam(value = "password", defaultValue = "") String password,
                                                 @RequestBody(required = false) IssuesGetRequest issuesGetRequest) {
        if (issuesGetRequest == null) {
            issuesGetRequest = new IssuesGetRequest();
        }
        issuesGetRequest.setUsername(username);
        issuesGetRequest.setPassword(password);
        userService.RequesterIsFound(issuesGetRequest);
        return new ResponseEntity<>(issueService.getIssues(projectId, issuesGetRequest), HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity<Issue> getIssue(@PathVariable("projectId") Long projectId,
                                          @PathVariable("issueId") Long issueId,
                                          @RequestParam(value = "username", defaultValue = "") String username,
                                          @RequestParam(value = "password", defaultValue = "") String password,
                                          @RequestBody(required = false) IssueGetRequest issueGetRequest) {
        if (issueGetRequest == null) {
            issueGetRequest = new IssueGetRequest();
        }
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
