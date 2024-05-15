package com.example.demo.dto.issue;

import com.example.demo.Interface.ToUser;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.entity.enumerate.IssueStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class IssuesGetRequest implements ToUser {
    private String username;
    private String password;

    private String title = null;
    private String reporter = null;
    private String fixer = null;
    private String assignee = null;
    private IssuePriority priority = null;
    private IssueStatus status = null;
    private LocalDateTime reportedDate = null;

    public User toUser(){
        return new User(username, password);
    }
}
