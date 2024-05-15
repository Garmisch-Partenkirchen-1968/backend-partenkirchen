package com.example.demo.dto.project;

import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import lombok.Getter;

import java.util.Optional;

@Getter
public class PermissionRequest {
    private String username;
    private String password;
    private boolean[] permissions;
}
