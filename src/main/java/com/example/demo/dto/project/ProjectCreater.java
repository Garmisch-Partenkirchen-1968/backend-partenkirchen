package com.example.demo.dto.project;

import com.example.demo.Interface.ToUser;
import com.example.demo.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectCreater implements ToUser {
    private String username;
    private String password;
    private String projectName;

    public User toUser(){
        return new User(username, password);
    }
}
