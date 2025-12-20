package com.example.network_homework.repository;

import com.example.network_homework.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(String userId);
    
    List<CartItem> findByProductId(Long productId);
}



