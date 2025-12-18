package com.example.network_homework.controller;

import com.example.network_homework.entity.UserAccount;
import com.example.network_homework.repository.UserAccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuthController {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static class RegisterRequest {
        public String username;
        public String password;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        Optional<UserAccount> existing = userAccountRepository.findByUsername(request.username);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.username);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole("ROLE_USER");

        UserAccount saved = userAccountRepository.save(user);
        saved.setPassword(null); // 返回时不暴露密码
        return ResponseEntity.ok(saved);
    }
}



