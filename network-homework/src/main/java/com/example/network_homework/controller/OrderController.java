package com.example.network_homework.controller;

import com.example.network_homework.entity.Order;
import com.example.network_homework.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        Order order = orderService.getOrderById(orderId);
        
        // 检查权限：只有订单所有者或管理员可以查看
        boolean isOwner = order.getUserId().equals(userDetails.getUsername());
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Order> payOrder(@PathVariable Long orderId,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        Order order = orderService.getOrderById(orderId);
        
        // 检查权限：只有订单所有者可以支付
        if (!order.getUserId().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).build();
        }
        
        Order paidOrder = orderService.payOrder(orderId);
        return ResponseEntity.ok(paidOrder);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        Order order = orderService.getOrderById(orderId);
        
        // 检查权限：只有订单所有者可以取消
        if (!order.getUserId().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).build();
        }
        
        Order cancelledOrder = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(cancelledOrder);
    }
}

