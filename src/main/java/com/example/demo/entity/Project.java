package com.example.demo.entity;

import com.example.demo.dto.project.ProjectGetResponse;
import com.example.demo.dto.project.ProjectsGetResponse;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    private String name;

    @Nonnull
    private String description;

    @OneToMany
    private List<Issue> issues = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_permissions", joinColumns = @JoinColumn(name = "project_id"))
    @MapKeyJoinColumn(name = "user_id")
    @Column(name = "permission")
    private Map<User, Integer> members = new HashMap<>();

    public ProjectsGetResponse toProjectsGetResponse() {
        return new ProjectsGetResponse(id, name, description);
    }

    public ProjectGetResponse toProjectGetResponse() {
        return new ProjectGetResponse(id, name, description, members);
    }
}
