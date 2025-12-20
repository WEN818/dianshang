package com.example.network_homework.controller;

import com.example.network_homework.entity.UserAccount;
import com.example.network_homework.entity.UserActivityLog;
import com.example.network_homework.repository.UserAccountRepository;
import com.example.network_homework.service.ActivityLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AuthController {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    public AuthController(UserAccountRepository userAccountRepository, 
                         PasswordEncoder passwordEncoder,
                         ActivityLogService activityLogService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.activityLogService = activityLogService;
    }

    public static class RegisterRequest {
        public String username;
        public String password;
        public String email;
    }
    
    public static class DeactivateRequest {
        public String password;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // 输入验证
        if (request.username == null || request.username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("用户名不能为空");
        }
        if (request.password == null || request.password.length() < 6) {
            return ResponseEntity.badRequest().body("密码长度至少6位");
        }
        if (request.email == null || request.email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("邮箱不能为空");
        }
        
        Optional<UserAccount> existing = userAccountRepository.findByUsername(request.username);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }

        UserAccount user = new UserAccount();
        user.setUsername(request.username);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole("ROLE_USER");
        user.setEmail(request.email);
        user.setEnabled(true);

        try {
            UserAccount saved = userAccountRepository.save(user);
            saved.setPassword(null); // 返回时不暴露密码
            
            // 记录注册日志
            try {
                activityLogService.logActivity(saved.getUsername(), 
                    UserActivityLog.ActivityType.REGISTER, 
                    "用户注册：" + saved.getUsername());
            } catch (Exception e) {
                System.err.println("记录注册日志失败: " + e.getMessage());
                // 日志记录失败不影响注册流程
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            System.err.println("注册失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("注册失败，请稍后重试");
        }
    }
    
    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateAccount(@RequestBody DeactivateRequest request,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body("未登录");
            }

            String username = userDetails.getUsername();
            Optional<UserAccount> userOpt = userAccountRepository.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("用户不存在");
            }

            UserAccount user = userOpt.get();
            
            // 验证密码
            if (!passwordEncoder.matches(request.password, user.getPassword())) {
                return ResponseEntity.status(403).body("密码错误");
            }

            // 标记为停用（软删除）
            user.setEnabled(false);
            userAccountRepository.save(user);

            // 记录日志
            try {
                activityLogService.logActivity(username, 
                    UserActivityLog.ActivityType.LOGOUT, 
                    "用户注销账户：" + username);
            } catch (Exception e) {
                System.err.println("注销日志记录失败: " + e.getMessage());
            }

            return ResponseEntity.ok().body("账户已注销");
        } catch (Exception e) {
            System.err.println("注销账户失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("注销失败，请稍后重试");
        }
    }
}



