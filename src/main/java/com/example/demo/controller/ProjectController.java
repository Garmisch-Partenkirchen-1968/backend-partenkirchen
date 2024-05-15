package com.example.demo.controller;

import com.example.demo.dto.project.GetPermissionDTO;
import com.example.demo.dto.project.PermissionRequest;
import com.example.demo.dto.project.ProjectCreater;
import com.example.demo.dto.user.UserSignInResponse;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Permission;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;

    @PostMapping("/projects")
    public Project createProject(@RequestBody ProjectCreater projectCreater) {
        String username = projectCreater.getUsername();
        String password = projectCreater.getPassword();
        User us = new User(username, password);
        UserSignInResponse user = userService.signInUser(us);
        if(user == null){
            throw new RuntimeException("user not found");
        }
        return projectService.createProject(projectCreater);
    }

    @PostMapping("/projects/{projectId}/permissions/{userId}")
    public Project addPermission(@RequestBody PermissionRequest permissionRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        if(!RequesterIsFound(permissionRequest.getUsername(), permissionRequest.getPassword())) {
            throw new RuntimeException("user not found");
        }
        return projectService.addPermission(projectId, userId, permissionRequest);
    }

    @PatchMapping("/projects/{projectId}/permissions/{userId}")
    public Project updatePermission(@RequestBody PermissionRequest permissionRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        if(!RequesterIsFound(permissionRequest.getUsername(), permissionRequest.getPassword())) {
            throw new RuntimeException("user not found");
        }
        return projectService.updatePermission(projectId, userId, permissionRequest);
    }

    @DeleteMapping("/projects/{projectId}/permissions/{userId}")
    public Project deletePermission(@RequestBody PermissionRequest permissionRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        if(!RequesterIsFound(permissionRequest.getUsername(), permissionRequest.getPassword())) {
            throw new RuntimeException("user not found");
        }
        return projectService.deletePermission(projectId, userId, permissionRequest);
    }

    @GetMapping("/projects/{projectId}/permissions/{userId}")
    public boolean[] getPermission(@RequestBody GetPermissionDTO getPermissionDTO, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId) {
        if(!RequesterIsFound(getPermissionDTO.getUsername(), getPermissionDTO.getPassword())) {
            throw new RuntimeException("user not found");
        }
        return projectService.getPermission(projectId, userId, getPermissionDTO);
    }

    public boolean RequesterIsFound(String username, String password){
        User requester = new User(username, password);
        UserSignInResponse founduser = userService.signInUser(requester);
        if(founduser == null) {return false;}
        return true;
    }
}
