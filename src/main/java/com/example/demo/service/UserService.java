package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User signUpUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("username already exists");
        }
        if (user.getPassword().isEmpty()) {
            throw new RuntimeException("password cannot be empty");
        }
        return userRepository.save(user);
    }

    public User signInUser(User user) {
        User foundUser = userRepository.findByUsername(user.getUsername()).orElseThrow();
        if (!foundUser.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("password does not match");
        }
        return foundUser;
    }
}
