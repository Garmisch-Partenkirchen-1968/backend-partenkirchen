package com.example.demo.service;

import com.example.demo.dto.project.*;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public Project createProject(ProjectPostRequest projectPostRequest) {
        // project name이 겹치는 지 검사
        Optional<Project> optionalProject = projectRepository.findByName(projectPostRequest.getName());
        if(optionalProject.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project name already exists");
        }

        User user = getUserByUsername(projectPostRequest.getUsername());

        // project 생성
        Project project = new Project(projectPostRequest.getName(), projectPostRequest.getDescription());
        project = projectRepository.save(project);

        // project 생성자에게 admin 권한 부여
        project.getMembers().put(user, 1 << 3);

        return projectRepository.save(project);
    }

    public List<ProjectsGetResponse> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        List<ProjectsGetResponse> projectsGetResponses = new ArrayList<>();

        for (Project project : projects) {
            projectsGetResponses.add(project.toProjectsGetResponse());
        }

        return projectsGetResponses;
    }

    private void updateProjectName(Project project, String newName) {
        if (newName.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project name cannot be empty");
        }

        Optional<Project> anotherProject = projectRepository.findByName(newName);
        if (anotherProject.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project name already exists");
        }

        project.setName(newName);
        projectRepository.save(project);
    }

    private void updateProjectDescription(Project project, String newDescription) {
        project.setDescription(newDescription);
        projectRepository.save(project);
    }

    public void patchProject(Long projectId, ProjectPatchRequest projectPatchRequest) {
        Project project = getProject(projectId);
        User user = getUserByUsername(projectPatchRequest.getUsername());

        if (project.getMembers().get(user) == null ||
                (project.getMembers().get(user) & (1 << 3)) == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you don't have permission to this project");
        }

        if (projectPatchRequest.getName() != null) {
            updateProjectName(project, projectPatchRequest.getName());
        }

        if (projectPatchRequest.getDescription() != null) {
            updateProjectDescription(project, projectPatchRequest.getDescription());
        }
    }

    public void deleteProject(Long projectId, String username) {
        Project project = getProject(projectId);
        User user = getUserByUsername(username);

        if (project.getMembers().get(user) == null ||
                (project.getMembers().get(user) & (1 << 3)) == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you don't have permission to this project");
        }

        projectRepository.delete(project);
    }

    public Project getProject(Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project not found");
        }
        return optionalProject.get();
    }

    private User getUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found");
        }
        return optionalUser.get();
    }


}
