package com.example.network_homework.config;

import com.example.network_homework.entity.UserActivityLog;
import com.example.network_homework.service.ActivityLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ActivityLogService activityLogService;

    public LoginSuccessHandler(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                       HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        
        // 调试日志：确认登录的用户名
        System.out.println("==========================================");
        System.out.println("用户登录成功");
        System.out.println("用户名: " + username);
        System.out.println("旧会话ID: " + (request.getSession(false) != null ? request.getSession(false).getId() : "无"));
        
        // 确保创建新会话（防止会话固定攻击和缓存问题）
        // 在登录时创建新会话，避免使用旧的会话数据
        jakarta.servlet.http.HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            // 保存需要保留的数据（如果有）
            oldSession.invalidate();
        }
        // 创建新会话
        jakarta.servlet.http.HttpSession newSession = request.getSession(true);
        // 在新会话中设置用户名（用于调试）
        newSession.setAttribute("username", username);
        
        System.out.println("新会话ID: " + newSession.getId());
        System.out.println("==========================================");
        
        // 记录登录日志
        try {
            activityLogService.logActivity(username, 
                UserActivityLog.ActivityType.LOGIN, 
                "用户登录：" + username);
        } catch (Exception e) {
            System.err.println("记录登录日志失败: " + e.getMessage());
            // 日志记录失败不影响登录流程
        }
        
        // 使用父类方法处理重定向（Spring Security会自动处理会话和认证）
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

