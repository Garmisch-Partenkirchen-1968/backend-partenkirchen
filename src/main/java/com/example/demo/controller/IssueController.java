package com.example.demo.controller;

import com.example.demo.dto.issue.*;
import com.example.demo.entity.Issue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class IssueController {
    @PostMapping("/projects/{projectId}/issues")
    public ResponseEntity<IssuePostResponse> postIssue(@PathVariable("projectId") Long projectId, @RequestBody IssuePostRequest issuePostRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/projects/{projectId}/issues")
    public ResponseEntity<List<Issue>> getIssues(@PathVariable("projectId") Long projectId, @RequestBody IssuesGetRequest issuesGetRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity<Issue> getIssue(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody IssueGetRequest issueGetRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @PatchMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity patchIssue(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody IssuePatchRequest issuePatchRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @DeleteMapping("/projects/{projectId}/issues/{issueId}")
    public ResponseEntity deleteIssue(@PathVariable("projectId") Long projectId, @PathVariable("issueId") Long issueId, @RequestBody IssueDeleteRequest issueDeleteRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
