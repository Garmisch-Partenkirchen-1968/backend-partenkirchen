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
        Long userid = userFindController.RequesterIsFound(issuePostRequest);
        return new ResponseEntity<> (issueService.postIssue(projectId, issuePostRequest), HttpStatus.CREATED);
    }

    @GetMapping("/projects/{projectId}/issues")
    public ResponseEntity<List<Issue>> getIssues(@PathVariable("projectId") Long projectId, @RequestBody IssuesGetRequest issuesGetRequest) {
        Long userid = userFindController.RequesterIsFound(issuesGetRequest);
        return new ResponseEntity<>(issueService.getIssues(projectId, issuesGetRequest), HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity<Issue> getIssue(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody IssueGetRequest issueGetRequest) {
        Long userid = userFindController.RequesterIsFound(issueGetRequest);
        return new ResponseEntity<>(issueService.getIssue(projectId, issueId, issueGetRequest), HttpStatus.OK);
    }

    @PatchMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity patchIssue(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody IssuePatchRequest issuePatchRequest) {
        Long userid = userFindController.RequesterIsFound(issuePatchRequest);
        issueService.patchIssue(projectId, issueId, issuePatchRequest);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity deleteIssue(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody IssueDeleteRequest issueDeleteRequest) {
        Long userid = userFindController.RequesterIsFound(issueDeleteRequest);
        issueService.deleteIssue(projectId, issueId, issueDeleteRequest);
        return new ResponseEntity(HttpStatus.OK);
    }
}
