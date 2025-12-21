package com.example.network_homework.util;

import com.example.network_homework.entity.Order;

public class OrderStatusUtil {

    public String getText(Order.OrderStatus status) {
        switch (status) {
            case PENDING_PAYMENT: return "待支付";
            case PAID: return "已支付";
            case SHIPPED: return "已发货";
            case COMPLETED: return "已完成";
            case CANCELLED: return "已取消";
            default: return "未知";
        }
    }

    public String getClass(Order.OrderStatus status) {
        switch (status) {
            case PENDING_PAYMENT: return "status-pending";
            case PAID: return "status-paid";
            case SHIPPED: return "status-shipped";
            case COMPLETED: return "status-completed";
            case CANCELLED: return "status-cancelled";
            default: return "";
        }
    }
}



