package com.example.demo.service;

import com.example.demo.controller.ProjectController;
import com.example.demo.dto.project.PermissionRequest;
import com.example.demo.dto.project.ProjectCreater;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Permission;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public Project createProject(ProjectCreater projectCreater){
        String projectName = projectCreater.getProjectName();
        String userName = projectCreater.getUsername();

        if(projectName.isEmpty()){
            throw new RuntimeException("project name is empty");
        }

        Optional<Project> proj = projectRepository.findByName(projectName);
        Optional<User> us = userRepository.findByUsername(userName);

        if(proj.isPresent()){throw new RuntimeException("project name already exists");}
        if(us.isEmpty()){throw new RuntimeException("user not found");}
        User user = us.get();

        Project project = new Project(projectName, user);
        return projectRepository.save(project);
    }

    public Project addPermission(Long projectId, Long userId, PermissionRequest permissionRequest){
        Optional<Project> proj = projectRepository.findById(projectId);
        Optional<User> us = userRepository.findById(userId);
        Optional<User> req = userRepository.findByUsername(permissionRequest.getReqName());

        Project project;
        User user, requester;
        if(req.isEmpty()){throw new RuntimeException("requester is not exist");}
        if(proj.isEmpty()){throw new RuntimeException("project not found");}
        if(us.isEmpty()){throw new RuntimeException("user is not exist"); }

        boolean[] permissions = permissionRequest.getPermissions();
        requester = req.get();
        project = proj.get();
        user = us.get();
        if(project.getMembers().get(user) != null){
            throw new RuntimeException("User already exsists in project");
        }
        if(projectRepository.findById(project.getId()).isEmpty()){
            throw new RuntimeException("Project not exists");
        }

        Integer permission = 0;
        if(permissions[0]) permission = permission | (1 << 3);
        if(permissions[1]) permission = permission | (1 << 2);
        if(permissions[2]) permission = permission | (1 << 1);
        if(permissions[3]) permission = permission | (1 << 0);
        if(permission == 0){
            if(project.getMembers().get(user) != null){
                project.getMembers().remove(user);
                return projectRepository.save(project);
            }
        }
        project.getMembers().put(user, permission);
        return projectRepository.save(project);
    }
}
