package com.example.network_homework.service;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.network_homework.entity.Order;
import com.example.network_homework.entity.UserAccount;
import com.example.network_homework.repository.UserAccountRepository;

/**
 * 邮件服务（真实实现）
 * 使用Spring Boot Mail发送真实邮件
 */
@Service
public class EmailService {

    private final UserAccountRepository userAccountRepository;
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.from:}")
    private String fromEmail;

    @Autowired
    public EmailService(UserAccountRepository userAccountRepository, JavaMailSender mailSender) {
        this.userAccountRepository = userAccountRepository;
        this.mailSender = mailSender;
    }

    /**
     * 发送订单确认邮件
     */
    public void sendOrderConfirmationEmail(Order order) {
        String recipientEmail = getUserEmailForOrder(order);
        if (recipientEmail == null || recipientEmail.isEmpty() || recipientEmail.contains("@example.com")) {
            System.err.println("用户 " + order.getUserId() + " 未提供有效邮箱地址，跳过订单确认邮件发送。");
            return;
        }
        
        try {
            String emailContent = buildOrderConfirmationEmail(order);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail != null && !fromEmail.isEmpty() ? fromEmail : "noreply@example.com");
            message.setTo(recipientEmail);
            message.setSubject("订单确认 - " + order.getOrderNumber());
            message.setText(emailContent);
            
            mailSender.send(message);
            
            System.out.println("==========================================");
            System.out.println("✓ 订单确认邮件发送成功");
            System.out.println("收件人：" + recipientEmail);
            System.out.println("订单号：" + order.getOrderNumber());
            System.out.println("==========================================");
        } catch (Exception e) {
            System.err.println("==========================================");
            System.err.println("✗ 订单确认邮件发送失败");
            System.err.println("收件人：" + recipientEmail);
            System.err.println("错误信息：" + e.getMessage());
            System.err.println("==========================================");
            e.printStackTrace();
        }
    }

    /**
     * 发送发货通知邮件
     */
    public void sendShippingNotificationEmail(Order order) {
        String recipientEmail = getUserEmailForOrder(order);
        if (recipientEmail == null || recipientEmail.isEmpty() || recipientEmail.contains("@example.com")) {
            System.err.println("用户 " + order.getUserId() + " 未提供有效邮箱地址，跳过发货通知邮件发送。");
            return;
        }
        
        try {
            String emailContent = buildShippingNotificationEmail(order);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail != null && !fromEmail.isEmpty() ? fromEmail : "noreply@example.com");
            message.setTo(recipientEmail);
            message.setSubject("您的订单已发货 - " + order.getOrderNumber());
            message.setText(emailContent);
            
            mailSender.send(message);
            
            System.out.println("==========================================");
            System.out.println("✓ 发货通知邮件发送成功");
            System.out.println("收件人：" + recipientEmail);
            System.out.println("订单号：" + order.getOrderNumber());
            System.out.println("==========================================");
        } catch (Exception e) {
            System.err.println("==========================================");
            System.err.println("✗ 发货通知邮件发送失败");
            System.err.println("收件人：" + recipientEmail);
            System.err.println("错误信息：" + e.getMessage());
            System.err.println("==========================================");
            e.printStackTrace();
        }
    }

    private String getUserEmailForOrder(Order order) {
        // 优先使用订单中保存的邮箱
        if (order.getUserEmail() != null && !order.getUserEmail().isEmpty()) {
            return order.getUserEmail();
        }
        // 如果订单中没有，尝试从用户账户中获取
        Optional<UserAccount> userOpt = userAccountRepository.findByUsername(order.getUserId());
        return userOpt.map(UserAccount::getEmail)
                .orElse(order.getUserId() + "@example.com"); // 如果没有邮箱，使用默认值
    }

    private String buildOrderConfirmationEmail(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("亲爱的 ").append(order.getReceiverName()).append("，\n\n");
        sb.append("感谢您的购买！您的订单已成功支付。\n\n");
        sb.append("订单信息：\n");
        sb.append("订单号：").append(order.getOrderNumber()).append("\n");
        sb.append("订单金额：¥").append(String.format("%.2f", order.getTotalAmount())).append("\n");
        sb.append("订单状态：").append(getStatusText(order.getStatus())).append("\n");
        sb.append("下单时间：").append(formatDateTime(order.getCreatedAt())).append("\n");
        sb.append("支付时间：").append(formatDateTime(order.getPaidAt())).append("\n\n");

        sb.append("收货信息：\n");
        sb.append("收货人：").append(order.getReceiverName()).append("\n");
        sb.append("联系电话：").append(order.getReceiverPhone()).append("\n");
        sb.append("收货地址：").append(order.getShippingAddress()).append("\n\n");

        sb.append("订单明细：\n");
        sb.append("----------------------------------------\n");
        order.getItems().forEach(item -> {
            sb.append("商品：").append(item.getProduct().getName()).append("\n");
            sb.append("数量：").append(item.getQuantity()).append("\n");
            sb.append("单价：¥").append(String.format("%.2f", item.getPrice())).append("\n");
            sb.append("小计：¥").append(String.format("%.2f", item.getSubtotal())).append("\n");
            sb.append("----------------------------------------\n");
        });

        sb.append("\n如有任何问题，请联系客服。\n");
        sb.append("再次感谢您的购买！\n");

        return sb.toString();
    }

    private String buildShippingNotificationEmail(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("亲爱的 ").append(order.getReceiverName()).append("，\n\n");
        sb.append("您的订单 ").append(order.getOrderNumber()).append(" 已发货！\n\n");
        sb.append("订单信息：\n");
        sb.append("订单号：").append(order.getOrderNumber()).append("\n");
        sb.append("订单金额：¥").append(String.format("%.2f", order.getTotalAmount())).append("\n");
        sb.append("订单状态：").append(getStatusText(order.getStatus())).append("\n");
        sb.append("下单时间：").append(formatDateTime(order.getCreatedAt())).append("\n");
        sb.append("支付时间：").append(formatDateTime(order.getPaidAt())).append("\n");
        sb.append("发货时间：").append(formatDateTime(order.getShippedAt())).append("\n\n");

        sb.append("收货信息：\n");
        sb.append("收货人：").append(order.getReceiverName()).append("\n");
        sb.append("联系电话：").append(order.getReceiverPhone()).append("\n");
        sb.append("收货地址：").append(order.getShippingAddress()).append("\n\n");

        sb.append("订单明细：\n");
        sb.append("----------------------------------------\n");
        order.getItems().forEach(item -> {
            sb.append("商品：").append(item.getProduct().getName()).append("\n");
            sb.append("数量：").append(item.getQuantity()).append("\n");
            sb.append("单价：¥").append(String.format("%.2f", item.getPrice())).append("\n");
            sb.append("小计：¥").append(String.format("%.2f", item.getSubtotal())).append("\n");
            sb.append("----------------------------------------\n");
        });

        sb.append("\n您的包裹正在路上，请注意查收！\n");
        sb.append("如有任何问题，请联系客服。\n");
        sb.append("再次感谢您的购买！\n");

        return sb.toString();
    }

    private String getStatusText(Order.OrderStatus status) {
        switch (status) {
            case PENDING_PAYMENT: return "待支付";
            case PAID: return "已支付";
            case SHIPPED: return "已发货";
            case COMPLETED: return "已完成";
            case CANCELLED: return "已取消";
            default: return "未知";
        }
    }

    private String formatDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "未设置";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(java.sql.Timestamp.valueOf(dateTime));
    }
}

