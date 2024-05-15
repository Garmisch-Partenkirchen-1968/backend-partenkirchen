package com.example.demo.dto.issue;

import com.example.demo.Interface.ToUser;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.entity.enumerate.IssueStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssuePatchRequest implements ToUser {
    private String username;
    private String password;

    private String title;
    private String assignee;
    private IssueStatus status;
    private IssuePriority priority;

    public User toUser(){
        return new User(username, password);
    }
}
