package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
