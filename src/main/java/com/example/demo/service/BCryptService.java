package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BCryptService {
    public String encodeBcrypt(String planeText, int strength) {
        return new BCryptPasswordEncoder(strength).encode(planeText);
    }
}
