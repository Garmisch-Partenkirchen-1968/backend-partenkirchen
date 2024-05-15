package com.example.demo.dto.project;

import com.example.demo.Interface.ToUser;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class PermissionRequest implements ToUser {
    private String username;
    private String password;
    private boolean[] permissions;

    public User toUser(){
        return new User(username, password);
    }
}
