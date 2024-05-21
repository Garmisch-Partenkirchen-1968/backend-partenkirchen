package com.example.demo.controller;

import com.example.demo.dto.project.ProjectPostRequest;
import com.example.demo.entity.Project;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final UserFindController userFindController;

    @PostMapping("/projects")
    public Project createProject(@RequestBody ProjectPostRequest projectCreater) {
        userFindController.RequesterIsFound(projectCreater);
        return projectService.createProject(projectCreater);
    }
}
