package com.example.demo.controller;

import com.example.demo.dto.project.*;
import com.example.demo.dto.user.Fixer;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserRecommendService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;
    private final UserRecommendService userRecommendService;

    @PostMapping("/projects")
    public ResponseEntity<ProjectGetResponse> createProject(@RequestBody ProjectPostRequest projectCreater) {
        userService.RequesterIsFound(projectCreater);
        return new ResponseEntity<>(projectService.createProject(projectCreater), HttpStatus.CREATED);
    }

    @GetMapping("/projects")
    public List<ProjectsGetResponse> getProjects(@RequestParam(value = "username", defaultValue = "") String username,
                                                 @RequestParam(value = "password", defaultValue = "") String password) {
        User user = new User(username, password);
        userService.RequesterIsFound(user);

        return projectService.getAllProjects();
    }

    @GetMapping("/projects/{projectId}")
    public ProjectGetResponse getProject(@PathVariable Long projectId,
                                         @RequestParam(value = "username", defaultValue = "") String username,
                                         @RequestParam(value = "password", defaultValue = "") String password) {
        User user = new User(username, password);
        userService.RequesterIsFound(user);

        return projectService.getProject(projectId).toProjectGetResponse();
    }

    @PatchMapping("/projects/{projectId}")
    public void patchProject(@PathVariable Long projectId, @RequestBody ProjectPatchRequest projectPatchRequest) {
        userService.RequesterIsFound(projectPatchRequest);

        projectService.patchProject(projectId, projectPatchRequest);
    }

    @DeleteMapping("/projects/{projectId}")
    public void deleteProject(@PathVariable Long projectId, @RequestBody ProjectDeleteRequest projectDeleteRequest) {
        userService.RequesterIsFound(projectDeleteRequest);

        projectService.deleteProject(projectId, projectDeleteRequest.getUsername());
    }

    @GetMapping("/projects/{projectId}/recommend-assignee")
    public List<Fixer> recommend3Assignee(@PathVariable Long projectId,
                                          @RequestParam(value = "username", defaultValue = "") String username,
                                          @RequestParam(value = "password", defaultValue = "") String password,
                                          @RequestParam(value = "priority") IssuePriority priority) {
        User user = new User(username, password);
        Long userId = userService.RequesterIsFound(user);
        
        return userRecommendService.getRecommendedAssigneeList(projectId, userId, priority).subList(0, 3);
    }
}
