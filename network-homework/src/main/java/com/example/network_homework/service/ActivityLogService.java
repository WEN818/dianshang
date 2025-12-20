package com.example.network_homework.service;

import org.springframework.stereotype.Service;

import com.example.network_homework.entity.UserActivityLog;
import com.example.network_homework.repository.UserActivityLogRepository;

@Service
public class ActivityLogService {

    private final UserActivityLogRepository activityLogRepository;

    public ActivityLogService(UserActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void logActivity(String userId, UserActivityLog.ActivityType activityType, String description) {
        try {
            UserActivityLog log = new UserActivityLog();
            log.setUserId(userId);
            log.setActivityType(activityType);
            log.setDescription(description);
            activityLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("记录活动日志失败: " + e.getMessage());
            // 日志记录失败不影响业务流程
        }
    }
}

