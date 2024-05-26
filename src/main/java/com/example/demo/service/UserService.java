package com.example.demo.service;

import com.example.demo.dto.user.UserSignInResponse;
import com.example.demo.dto.user.UserSignupRequest;
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
    private final BCryptService bCryptService;

    @Autowired
    public UserService(UserRepository userRepository, BCryptService bCryptService) {
        this.userRepository = userRepository;
        this.bCryptService = bCryptService;
    }

    public User signUpUser(UserSignupRequest userSignupRequest) {
        if (userRepository.findByUsername(userSignupRequest.getUsername()).isPresent()) {
            System.out.println("username already exists");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username already exists");
        }
        if (userSignupRequest.getUsername().isEmpty() || userSignupRequest.getPassword().isEmpty()) {
            System.out.println("field cannot be empty");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "field cannot be empty");
        }
        if (userSignupRequest.getUsername().matches(".*[ \\t\\n\\r].*")) {
            System.out.println("whitespace in username");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "whitespace in username");
        }
        String encodedPassword = bCryptService.encodeBcrypt(userSignupRequest.getPassword());
        User encryptedUser = new User(userSignupRequest.getUsername(), encodedPassword);
        return userRepository.save(encryptedUser);
    }

    // 여기서부터 hashing 더 해야 함
    public UserSignInResponse signInUser(User user) {
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());
        if (foundUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        if (!bCryptService.matchesBcrypt(user.getPassword(), foundUser.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return new UserSignInResponse(foundUser.get().getId());
    }

    public void updatePassword(Long userId, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password cannot be empty");
        }
        User user = userRepository.findById(userId).orElseThrow();

        String encodedPassword = bCryptService.encodeBcrypt(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        userRepository.delete(user);
    }

    public List<User> getUsers(String keyword) {
        return userRepository.findByUsernameContaining(keyword);
    }
}