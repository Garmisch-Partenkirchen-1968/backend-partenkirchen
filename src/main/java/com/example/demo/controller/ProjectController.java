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

    @PostMapping("/project")
    public Project createProject(@RequestBody Long userid, @RequestBody String projectName) {
        return projectService.createProject(userid, projectName);
    }

    @PostMapping("/projects/{projectId}/permissions/{userId}")
    public Project addPermission(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId, @RequestBody PermissionRequest permissionRequest){
        User requester = new User(permissionRequest.getReqName(), permissionRequest.getPassword());
        User founduser = userService.signInUser(requester);
        if(founduser == null) return null;

        return projectService.addPermission(projectId, userId, permissionRequest);
    }
}