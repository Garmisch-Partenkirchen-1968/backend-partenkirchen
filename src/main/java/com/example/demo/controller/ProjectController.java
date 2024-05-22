package com.example.demo.controller;

import com.example.demo.dto.project.*;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/projects")
    public List<ProjectsGetResponse> getProjects(@RequestParam(value = "username", defaultValue = "") String username,
                                                 @RequestParam(value = "password", defaultValue = "") String password) {
        User user = new User(username, password);
        userFindController.RequesterIsFound(user);

        return projectService.getAllProjects();
    }

    @GetMapping("/projects/{projectId}")
    public ProjectGetResponse getProject(@PathVariable Long projectId,
                                         @RequestParam(value = "username", defaultValue = "") String username,
                                         @RequestParam(value = "password", defaultValue = "") String password) {
        User user = new User(username, password);
        userFindController.RequesterIsFound(user);

        return projectService.getProject(projectId).toProjectGetResponse();
    }

    @PatchMapping("/projects/{projectId}")
    public void patchProject(@PathVariable Long projectId, @RequestBody ProjectPatchRequest projectPatchRequest) {
        userFindController.RequesterIsFound(projectPatchRequest);

        projectService.patchProject(projectId, projectPatchRequest);
    }

    @DeleteMapping("/projects/{projectId}")
    public void deleteProject(@PathVariable Long projectId, @RequestBody ProjectDeleteRequest projectDeleteRequest) {
        userFindController.RequesterIsFound(projectDeleteRequest);

        projectService.deleteProject(projectId, projectDeleteRequest.getUsername());
    }
}
