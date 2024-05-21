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
    private final UserFindController userFindController;

    @PostMapping("/projects/{projectId}/issues")
    public ResponseEntity<IssuePostResponse> postIssue(@PathVariable("projectId") Long projectId, @RequestBody IssuePostRequest issuePostRequest) {
        userFindController.RequesterIsFound(issuePostRequest);
        return issueService.postIssue(projectId, issuePostRequest);
    }

    @GetMapping("/projects/{projectId}/issues")
    public ResponseEntity<List<Issue>> getIssues(@PathVariable("projectId") Long projectId,
                                                 @RequestParam(value = "username", defaultValue = "") String username,
                                                 @RequestParam(value = "password", defaultValue = "") String password,
                                                 @RequestBody IssuesGetRequest issuesGetRequest) {
        issuesGetRequest.setUsername(username);
        issuesGetRequest.setPassword(password);
        userFindController.RequesterIsFound(issuesGetRequest);
        return issueService.getIssues(projectId, issuesGetRequest);
    }

    @GetMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity<Issue> getIssue(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody IssueGetRequest issueGetRequest) {
        userFindController.RequesterIsFound(issueGetRequest);
        return issueService.getIssue(projectId, issueId, issueGetRequest);
    }

    @PatchMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity patchIssue(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody IssuePatchRequest issuePatchRequest) {
        userFindController.RequesterIsFound(issuePatchRequest);
        return issueService.patchIssue(projectId, issueId, issuePatchRequest);
    }

    @DeleteMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity deleteIssue(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody IssueDeleteRequest issueDeleteRequest) {
        userFindController.RequesterIsFound(issueDeleteRequest);
        return issueService.deleteIssue(projectId, issueId, issueDeleteRequest);
    }
}
