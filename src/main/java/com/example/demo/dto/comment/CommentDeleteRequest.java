package com.example.demo.dto.comment;

import com.example.demo.Interface.ToUser;
import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
@AllArgsConstructor
public class CommentDeleteRequest implements ToUser {
    private String username;
    private String password;

    @Override
    public User toUser() {
        if (username == null || password == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return new User(username, password);
    }
}
