package com.example.demo.entity;

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

    @OneToMany
    private List<Issue> issues = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_permissions", joinColumns = @JoinColumn(name = "project_id"))
    @MapKeyJoinColumn(name = "user_id")
    @Column(name = "permission")
    private Map<User, Integer> members = new HashMap<>();

    @Nonnull
    @ManyToOne
    private User owner;

    public void setOwner(User user){
        this.owner = user;
    }
}
