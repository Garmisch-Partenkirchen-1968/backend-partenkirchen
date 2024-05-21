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
    private final UserFindController userFindController;

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
        Long userid = userFindController.RequesterIsFound(permissionRequest);
        return projectService.addPermission(projectId, userId, permissionRequest);
    }

    @PatchMapping("/projects/{projectId}/permissions/{userId}")
    public Project updatePermission(@RequestBody PermissionRequest permissionRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        Long userid = userFindController.RequesterIsFound(permissionRequest);
        return projectService.updatePermission(projectId, userId, permissionRequest);
    }

    @DeleteMapping("/projects/{projectId}/permissions/{userId}")
    public Project deletePermission(@RequestBody PermissionRequest permissionRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        Long userid = userFindController.RequesterIsFound(permissionRequest);
        return projectService.deletePermission(projectId, userId, permissionRequest);
    }

    @GetMapping("/projects/{projectId}/permissions/{userId}")
    public boolean[] getPermission(@RequestBody GetPermissionDTO getPermissionDTO,
                                   @RequestParam(value = "username", defaultValue = "") String username,
                                   @RequestParam(value = "password", defaultValue = "") String password,
                                   @PathVariable("projectId") Long projectId,
                                   @PathVariable("userId") Long userId) {
        getPermissionDTO.setUsername(username);
        getPermissionDTO.setPassword(password);
        Long userid = userFindController.RequesterIsFound(getPermissionDTO);
        return projectService.getPermission(projectId, userId, getPermissionDTO);
    }
}
