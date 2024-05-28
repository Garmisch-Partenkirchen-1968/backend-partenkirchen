package com.example.demo.entity;

import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.entity.enumerate.IssueStatus;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    private String title;

    @Nonnull
    @ManyToOne
    private User reporter;

    @CreatedDate
    private LocalDateTime reportedDate;

    @ManyToOne
    private User fixer;

    @ManyToOne
    private User assignee;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private IssuePriority priority;

    @Nonnull
    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    @OneToMany
    private List<Comment> comments;
}
