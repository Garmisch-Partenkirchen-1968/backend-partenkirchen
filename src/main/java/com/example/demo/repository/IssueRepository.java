package com.example.demo.repository;

import com.example.demo.entity.Issue;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.entity.enumerate.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    public Optional<Issue> findById(Long id);
    public Optional<Issue> findByTitle(String title);
    public Optional<List<Issue>> findByReporter(User reporter);
    public Optional<List<Issue>> findByReportedDate(LocalDateTime repoetedDate);
    public Optional<List<Issue>> findByFixer(User fixer);
    public Optional<List<Issue>> findByAssignee(User assignee);
    public Optional<List<Issue>> findByPriority(IssuePriority priority);
    public Optional<List<Issue>> findByStatus(IssueStatus status);
}
