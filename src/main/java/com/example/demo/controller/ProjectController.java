package com.example.demo.controller;

import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/createProject")
    public Project createProject(@RequestBody Project project, User user){
        return projectService.createProject(project, user);
    }

    @PostMapping("/addPermission")
    public Project addPermission(@RequestBody Project project, User user, boolean[] permissions){
        //Check 표시로 admin, PL, tester, DEV의 boolean값을 array로 받아옴
        return projectService.addPermission(project, user, permissions);
    }

    @PostMapping("/deletePermission")
    public Project deletePermission(@RequestBody Project project, User requester, User user){
        return projectService.deletePermission(project, requester, user);
    }
}
