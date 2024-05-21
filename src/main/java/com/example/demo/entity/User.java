package com.example.demo.entity;

import com.example.demo.Interface.ToUser;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "user_table")
public class User implements ToUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    @Column(unique = true)
    private String username;

    @Nonnull
    private String password;

    @Override
    public User toUser() {
        return new User(username, password);
    }
}
