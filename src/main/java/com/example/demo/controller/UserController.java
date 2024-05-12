package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public void delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
