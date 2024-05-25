package com.example.demo.controller;

import com.example.demo.dto.Permission.PermissionDeleteRequest;
import com.example.demo.dto.Permission.PermissionPatchRequest;
import com.example.demo.dto.Permission.PermissionPostRequest;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class PermissionController {
    private final UserFindController userFindController;
    private final PermissionService permissionService;
    private final UserRepository userRepository;

    @PostMapping("/projects/{projectId}/permissions/{userId}")
    public ResponseEntity<boolean[]> addPermission(@RequestBody PermissionPostRequest permissionPostRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        userFindController.RequesterIsFound(permissionPostRequest);
        return new ResponseEntity<>(permissionService.addPermission(projectId, userId, permissionPostRequest), HttpStatus.OK);
    }

    @PatchMapping("/projects/{projectId}/permissions/{userId}")
    public ResponseEntity<boolean[]> updatePermission(@RequestBody PermissionPatchRequest permissionPatchRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        userFindController.RequesterIsFound(permissionPatchRequest);
        return new ResponseEntity<>(permissionService.updatePermission(projectId, userId, permissionPatchRequest), HttpStatus.OK);
    }

    @DeleteMapping("/projects/{projectId}/permissions/{userId}")
    public ResponseEntity deletePermission(@RequestBody PermissionDeleteRequest permissionDeleteRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        userFindController.RequesterIsFound(permissionDeleteRequest);
        permissionService.deletePermission(projectId, userId, permissionDeleteRequest);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}/permissions/{userId}")
    public ResponseEntity<boolean[]> getPermission(@RequestParam(value = "username", defaultValue = "") String username,
                                   @RequestParam(value = "password", defaultValue = "") String password,
                                   @PathVariable("projectId") Long projectId,
                                   @PathVariable("userId") Long userId) {
        User user = new User(username, password);
        Long requesterId = userFindController.RequesterIsFound(user);
        return new ResponseEntity<>(permissionService.getPermission(projectId, userId, requesterId), HttpStatus.OK);
    }
}
