package com.example.demo.controller;

import com.example.demo.dto.project.GetPermissionDTO;
import com.example.demo.dto.project.PermissionRequest;
import com.example.demo.entity.Project;
import com.example.demo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PermissionController {
    private final UserFindController userFindController;
    private final ProjectService projectService;

    @PostMapping("/projects/{projectId}/permissions/{userId}")
    public Project addPermission(@RequestBody PermissionRequest permissionRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        userFindController.RequesterIsFound(permissionRequest);
        return projectService.addPermission(projectId, userId, permissionRequest);
    }

    @PatchMapping("/projects/{projectId}/permissions/{userId}")
    public Project updatePermission(@RequestBody PermissionRequest permissionRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        userFindController.RequesterIsFound(permissionRequest);
        return projectService.updatePermission(projectId, userId, permissionRequest);
    }

    @DeleteMapping("/projects/{projectId}/permissions/{userId}")
    public Project deletePermission(@RequestBody PermissionRequest permissionRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        userFindController.RequesterIsFound(permissionRequest);
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
        userFindController.RequesterIsFound(getPermissionDTO);
        return projectService.getPermission(projectId, userId, getPermissionDTO);
    }
}
