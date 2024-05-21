package com.example.demo.dto.issue;

import com.example.demo.Interface.ToUser;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.entity.enumerate.IssueStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class IssuesGetRequest implements ToUser {
    private String username;
    private String password;

    private String title;
    private String reporter;
    private String fixer;
    private String assignee;
    private IssuePriority priority;
    private IssueStatus status;
    private LocalDateTime reportedDate;

    public User toUser(){
        if(username == null || password == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return new User(username, password);
    }
}
