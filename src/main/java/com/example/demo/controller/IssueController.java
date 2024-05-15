package com.example.demo.controller;

import com.example.demo.dto.issue.IssuePostRequest;
import com.example.demo.dto.issue.IssuePostResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssueController {
    @PostMapping("/projects/{projectId}/issues")
    public ResponseEntity<IssuePostResponse> postIssue(@PathVariable("projectId") Long projectId, @RequestBody IssuePostRequest issuePostRequest) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
