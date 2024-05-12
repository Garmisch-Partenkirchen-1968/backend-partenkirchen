package com.example.demo.service;

import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public Project createProject(Project project, User user){
        if(projectRepository.findByProjectname(project.getName()).isPresent()){
            throw new RuntimeException("project name already exists");
        }
        else if(project.getName().isEmpty()){
            throw new RuntimeException("proejct name is empty");
        }
        project.setOwner(user); // owner 설정
        return projectRepository.save(project);
    }

    public Project addPermission(Project project, User user, boolean[] permissions){
        if(userRepository.findById(user.getId()).isPresent()){
            throw new RuntimeException("User already exsists in project");
        }
        if(projectRepository.findByProject(project).isEmpty()){
            throw new RuntimeException("Project not exists");
        }

        Integer permission = 0;
        if(permissions[0]) permission = permission | (1 << 3);
        if(permissions[1]) permission = permission | (1 << 2);
        if(permissions[2]) permission = permission | (1 << 1);
        if(permissions[3]) permission = permission | (1 << 0);
        project.getMembers().put(user, permission);
        return projectRepository.save(project);
    }

    public Project deletePermission(Project project, User requester, User user){
        if(project.getMembers().get(requester) < (1 << 3)){
            throw new RuntimeException("User does not have permission to delete user");
        }
        if(project.getMembers().get(user) == null){
            throw new RuntimeException("There's no user in project");
        }
        if(projectRepository.findByProject(project).isEmpty()){
            throw new RuntimeException("Project not exists");
        }

        project.getMembers().remove(user);
        return projectRepository.save(project);
    }
}
