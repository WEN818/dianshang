package com.example.network_homework.service;

import com.example.network_homework.entity.*;
import com.example.network_homework.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository, 
                       OrderItemRepository orderItemRepository,
                       CartItemRepository cartItemRepository,
                       EmailService emailService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Order createOrder(String userId, String receiverName, String receiverPhone, 
                           String shippingAddress, String userEmail, List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("购物车为空");
        }

        Order order = new Order();
        order.setOrderNumber("ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setUserId(userId);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setShippingAddress(shippingAddress);
        order.setUserEmail(userEmail);
        order.setStatus(Order.OrderStatus.PENDING_PAYMENT);

        double totalAmount = 0.0;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getDeleted() != null && product.getDeleted()) {
                continue; // 跳过已删除的商品
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setSubtotal(product.getPrice() * cartItem.getQuantity());
            
            totalAmount += orderItem.getSubtotal();
            order.getItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // 保存订单项
        for (OrderItem item : savedOrder.getItems()) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }

        // 清空购物车
        cartItemRepository.deleteAll(cartItems);

        return savedOrder;
    }

    @Transactional
    public Order payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        if (order.getStatus() != Order.OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("订单状态不正确，无法支付");
        }

        order.setStatus(Order.OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        // 发送订单确认邮件
        emailService.sendOrderConfirmationEmail(savedOrder);

        return savedOrder;
    }

    @Transactional
    public Order shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        if (order.getStatus() != Order.OrderStatus.PAID) {
            throw new IllegalStateException("订单状态不正确，无法发货");
        }

        order.setStatus(Order.OrderStatus.SHIPPED);
        order.setShippedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        // 发送发货通知邮件
        emailService.sendShippingNotificationEmail(savedOrder);

        return savedOrder;
    }

    @Transactional
    public Order completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        if (order.getStatus() != Order.OrderStatus.SHIPPED) {
            throw new IllegalStateException("订单状态不正确，无法完成");
        }

        order.setStatus(Order.OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        if (order.getStatus() == Order.OrderStatus.COMPLETED || 
            order.getStatus() == Order.OrderStatus.SHIPPED) {
            throw new IllegalStateException("订单已发货或已完成，无法取消");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(String userId) {
        try {
            return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } catch (Exception e) {
            System.err.println("获取用户订单失败: " + e.getMessage());
            return List.of();
        }
    }

    public List<Order> getAllOrders() {
        try {
            return orderRepository.findAllByOrderByCreatedAtDesc();
        } catch (Exception e) {
            System.err.println("获取所有订单失败: " + e.getMessage());
            return List.of();
        }
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
    }

    public SalesStatistics getSalesStatistics() {
        try {
            List<Order> allOrders = orderRepository.findAll();
            long totalOrders = allOrders.size();
            long paidOrders = allOrders.stream()
                    .filter(o -> o.getStatus() == Order.OrderStatus.PAID || 
                               o.getStatus() == Order.OrderStatus.SHIPPED ||
                               o.getStatus() == Order.OrderStatus.COMPLETED)
                    .count();
            double totalRevenue = allOrders.stream()
                    .filter(o -> o.getStatus() == Order.OrderStatus.PAID || 
                               o.getStatus() == Order.OrderStatus.SHIPPED ||
                               o.getStatus() == Order.OrderStatus.COMPLETED)
                    .mapToDouble(Order::getTotalAmount)
                    .sum();

            return new SalesStatistics(totalOrders, paidOrders, totalRevenue);
        } catch (Exception e) {
            System.err.println("获取销售统计失败: " + e.getMessage());
            return new SalesStatistics(0, 0, 0.0);
        }
    }

    public static class SalesStatistics {
        private final long totalOrders;
        private final long paidOrders;
        private final double totalRevenue;

        public SalesStatistics(long totalOrders, long paidOrders, double totalRevenue) {
            this.totalOrders = totalOrders;
            this.paidOrders = paidOrders;
            this.totalRevenue = totalRevenue;
        }

        public long getTotalOrders() {
            return totalOrders;
        }

        public long getPaidOrders() {
            return paidOrders;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }
    }
}

