package com.example.demo.repository;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
