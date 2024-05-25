package com.example.demo.service;

import com.example.demo.dto.user.UserSignInResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signUpUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username already exists");
        }
        if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "field cannot be empty");
        }
        if (user.getUsername().matches(".*[ \\t\\n\\r].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "whitespace in username");
        }
        return userRepository.save(user);
    }

    public UserSignInResponse signInUser(User user) {
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());
        if (foundUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        if (!foundUser.get().getPassword().equals(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        return new UserSignInResponse(foundUser.get().getId());
    }

    public void updatePassword(Long userId, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password cannot be empty");
        }
        User user = userRepository.findById(userId).orElseThrow();
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        userRepository.delete(user);
    }

    public List<User> getUsers(String keyword) {
        List<User> users = userRepository.findAll();
        users.removeIf(user -> !(user.getUsername().contains(keyword)));
        return users;
    }
}
