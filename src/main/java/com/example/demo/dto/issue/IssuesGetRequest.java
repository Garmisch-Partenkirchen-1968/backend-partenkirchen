package com.example.demo.dto.issue;

import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.entity.enumerate.IssueStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssuesGetRequest {
    private String username;
    private String password;

    private String title;
    private String reporter;
    private String fixer;
    private String assignee;
    private IssuePriority priority;
    private IssueStatus status;
}
