package com.example.demo.controller;

import com.example.demo.dto.project.PermissionRequest;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Permission;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;

    /*@PostMapping("/project")
    public Project createProject(@RequestBody PermissionRequest permissionRequest) {
        return projectService.createProject(permissionRequest);
    }

    @PostMapping("/projects/permissions/{userId}")
    public Project addPermission(@PathVariable("userId") Long userid, @RequestBody PermissionRequest permissionRequest){
        User founduser = userService.signInUser()
    }*/
}
