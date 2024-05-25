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
    public Project addPermission(@RequestBody PermissionPostRequest permissionPostRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        userFindController.RequesterIsFound(permissionPostRequest);
        return permissionService.addPermission(projectId, userId, permissionPostRequest);
    }

    @PatchMapping("/projects/{projectId}/permissions/{userId}")
    public Project updatePermission(@RequestBody PermissionPatchRequest permissionPatchRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        userFindController.RequesterIsFound(permissionPatchRequest);
        return permissionService.updatePermission(projectId, userId, permissionPatchRequest);
    }

    @DeleteMapping("/projects/{projectId}/permissions/{userId}")
    public Project deletePermission(@RequestBody PermissionDeleteRequest permissionDeleteRequest, @PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId){
        userFindController.RequesterIsFound(permissionDeleteRequest);
        return permissionService.deletePermission(projectId, userId, permissionDeleteRequest);
    }

    @GetMapping("/projects/{projectId}/permissions/{userId}")
    public boolean[] getPermission(@RequestParam(value = "username", defaultValue = "") String username,
                                   @RequestParam(value = "password", defaultValue = "") String password,
                                   @PathVariable("projectId") Long projectId,
                                   @PathVariable("userId") Long userId) {
        Optional<User> OptionalUser = userRepository.findByUsername(username);
        if(OptionalUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found");
        }
        User requester = OptionalUser.get();
        if(!requester.getPassword().equals(password)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "wrong username or wrong password");
        }
        return permissionService.getPermission(projectId, userId, requester);
    }
}
