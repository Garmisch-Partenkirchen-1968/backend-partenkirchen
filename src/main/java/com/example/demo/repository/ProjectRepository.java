package com.example.demo.repository;

import com.example.demo.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByProjectname(String projectname);
    Optional<Project> findByProjectID(Long projectID);
}
