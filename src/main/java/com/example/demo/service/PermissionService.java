package com.example.demo.service;

import com.example.demo.dto.Permission.PermissionDeleteRequest;
import com.example.demo.dto.Permission.PermissionPatchRequest;
import com.example.demo.dto.Permission.PermissionPostRequest;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;


    public Project addPermission(Long projectId, Long userId, PermissionPostRequest permissionPostRequest) {
        Optional<Project> proj = projectRepository.findById(projectId);
        Optional<User> us = userRepository.findById(userId);
        Optional<User> req = userRepository.findByUsername(permissionPostRequest.getUsername());

        Project project;
        User user, requester;
        if(req.isEmpty()){throw new RuntimeException("requester is not exist");}
        if(proj.isEmpty()){throw new RuntimeException("project not found");}
        if(us.isEmpty()){throw new RuntimeException("user is not exist"); }

        boolean[] permissions = permissionPostRequest.getPermissions();
        requester = req.get();
        project = proj.get();
        user = us.get();
        if(project.getMembers().get(user) != null){
            throw new RuntimeException("User already exsists in project");
        }
        if(!hasPermisiion(project, requester)){
            throw new RuntimeException("requester has not permission");
        }
        Integer permission = 0;
        if(permissions[0]) permission = permission | (1 << 3);
        if(permissions[1]) permission = permission | (1 << 2);
        if(permissions[2]) permission = permission | (1 << 1);
        if(permissions[3]) permission = permission | (1 << 0);
        if(permission == 0){
            throw new RuntimeException("permission should not be zero");
        }
        project.getMembers().put(user, permission);
        return projectRepository.save(project);
    }

    public Project updatePermission(Long projectId, Long userId, PermissionPatchRequest permissionPatchRequest) {
        Optional<Project> proj = projectRepository.findById(projectId);
        Optional<User> us = userRepository.findById(userId);
        Optional<User> req = userRepository.findByUsername(permissionPatchRequest.getUsername());

        Project project;
        User user, requester;
        if(req.isEmpty()){throw new RuntimeException("requester is not exist");}
        if(proj.isEmpty()){throw new RuntimeException("project not found");}
        if(us.isEmpty()){throw new RuntimeException("user is not exist"); }

        boolean[] permissions = permissionPatchRequest.getPermissions();
        requester = req.get();
        project = proj.get();
        user = us.get();
        if(project.getMembers().get(user) == null){
            throw new RuntimeException("Bad Request: User not exsists in project");
        }
        if(!hasPermisiion(project, requester)){
            throw new RuntimeException("Bad Request: requester has not permission");
        }
        Integer permission = 0;
        if(permissions[0]) permission = permission | (1 << 3);
        if(permissions[1]) permission = permission | (1 << 2);
        if(permissions[2]) permission = permission | (1 << 1);
        if(permissions[3]) permission = permission | (1 << 0);
        if(permission == 0){
            throw new RuntimeException("permission should not be zero");
        }
        project.getMembers().remove(user);
        project.getMembers().put(user, permission);
        return projectRepository.save(project);
    }

    public Project deletePermission(Long projectId, Long userId, PermissionDeleteRequest permissionDeleteRequest) {
        Project project = getProject(projectId);
        User requester = getUserByUsername(permissionDeleteRequest.getUsername());
        User user = getUserById(userId);

        if(project.getMembers().get(user) == null){
            throw new RuntimeException("Bad Request: user not exsists in project");
        }
        if(!hasPermisiion(project, requester)){
            throw new RuntimeException("Bad Request: requester has not permission");
        }
        project.getMembers().remove(user);
        return projectRepository.save(project);
    }

    public boolean[] getPermission(Long projectId, Long userId, User requester) {
        Project project = getProject(projectId);
        User user = getUserById(userId);

        if(project.getMembers().get(user) == null){
            throw new RuntimeException("User not exsists in project");
        }
        if(!hasPermisiion(project, requester)){
            if(requester.getId() != userId) throw new RuntimeException("requester has not permission");
        }
        boolean[] permissions = new boolean[4];
        int permission = project.getMembers().get(user);

        if(permission >= (1 << 3)){
            permissions[0] = true;
            permission -= (1 << 3);
        }
        else permissions[0] = false;

        if(permission >= (1 << 2)){
            permissions[1] = true;
            permission -= (1 << 2);
        }
        else permissions[1] = false;

        if(permission >= (1 << 1)){
            permissions[2] = true;
            permission -= (1 << 1);
        }
        else permissions[2] = false;

        if(permission >= (1 << 0)){
            permissions[3] = true;
            permission -= (1 << 0);
        }
        else permissions[3] = false;

        return permissions;
    }

    public boolean hasPermisiion(Project project, User user) {
        return project.getMembers().get(user) >= (1 << 3);
    }

    private User getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found");
        }
        return optionalUser.get();
    }

    private User getUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found");
        }
        return optionalUser.get();
    }

    public Project getProject(Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project not found");
        }
        return optionalProject.get();
    }



}
