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
        if(project.getMembers().get(user) != null){
            throw new RuntimeException("User already exsists in project");
        }
        if(projectRepository.findByProjectID(project.getId()).isEmpty()){
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
