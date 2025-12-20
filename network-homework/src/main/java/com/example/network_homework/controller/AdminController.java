package com.example.network_homework.controller;

import com.example.network_homework.entity.Order;
import com.example.network_homework.entity.UserAccount;
import com.example.network_homework.repository.UserAccountRepository;
import com.example.network_homework.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final OrderService orderService;
    private final UserAccountRepository userAccountRepository;

    public AdminController(OrderService orderService, UserAccountRepository userAccountRepository) {
        this.orderService = orderService;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PostMapping("/orders/{orderId}/ship")
    public ResponseEntity<?> shipOrder(@PathVariable Long orderId) {
        try {
            Order shippedOrder = orderService.shipOrder(orderId);
            return ResponseEntity.ok(shippedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("发货失败：" + e.getMessage());
        }
    }

    @PostMapping("/orders/{orderId}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable Long orderId) {
        try {
            Order completedOrder = orderService.completeOrder(orderId);
            return ResponseEntity.ok(completedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("操作失败：" + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<OrderService.SalesStatistics> getStatistics() {
        return ResponseEntity.ok(orderService.getSalesStatistics());
    }

    @GetMapping("/customers")
    public ResponseEntity<List<UserAccount>> getAllCustomers() {
        return ResponseEntity.ok(userAccountRepository.findAll());
    }

    @GetMapping("/customers/{username}")
    public ResponseEntity<UserAccount> getCustomer(@PathVariable String username) {
        Optional<UserAccount> user = userAccountRepository.findByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/customers/{username}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable String username) {
        Optional<UserAccount> userOpt = userAccountRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        UserAccount user = userOpt.get();
        user.setEnabled(false);
        userAccountRepository.save(user);
        
        return ResponseEntity.ok().build();
    }
}

