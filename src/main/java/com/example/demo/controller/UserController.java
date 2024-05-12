package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        return userService.signUpUser(user);
    }

    @GetMapping("/signin")
    public User signin(@RequestBody User user) {
        return userService.signInUser(user);
    }

    @DeleteMapping("/user/{userId}")
    public void delete(@PathVariable Long userId, @RequestBody User user) {
        checkPermission(userId, user);
        userService.deleteUser(userId);
    }

    private void checkPermission(Long userId, User user) {
        User foundUser = userService.signInUser(user);
        if (!Objects.equals(foundUser.getId(), userId)) {
            throw new RuntimeException("not authorized");
        }
    }
}
