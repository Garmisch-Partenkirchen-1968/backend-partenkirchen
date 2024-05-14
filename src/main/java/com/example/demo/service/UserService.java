package com.example.demo.service;

import com.example.demo.dto.user.UserSignInResponse;
import com.example.demo.dto.user.UserUpdatePasswordRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.net.http.HttpClient;
import java.util.Optional;

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
        if (newPassword.isEmpty()) {
            throw new RuntimeException("password cannot be empty");
        }
        User user = userRepository.findById(userId).orElseThrow();
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        userRepository.delete(user);
    }
}
