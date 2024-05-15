package com.example.demo.dto.issue;

import com.example.demo.Interface.ToUser;
import com.example.demo.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueDeleteRequest implements ToUser {
    private String username;
    private String password;

    public User toUser(){
        return new User(username, password);
    }
}
