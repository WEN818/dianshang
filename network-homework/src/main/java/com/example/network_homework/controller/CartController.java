package com.example.network_homework.controller;

import com.example.network_homework.entity.CartItem;
import com.example.network_homework.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public List<CartItem> getCart(@RequestParam String userId) {
        return cartService.getCartItems(userId);
    }

    public static class AddToCartRequest {
        public String userId;
        public Long productId;
        public Integer quantity;
    }

    @PostMapping
    public CartItem addToCart(@RequestBody AddToCartRequest request) {
        return cartService.addToCart(request.userId, request.productId, request.quantity);
    }

    public static class UpdateQuantityRequest {
        public Integer quantity;
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable Long id, @RequestBody UpdateQuantityRequest request) {
        return ResponseEntity.ok(cartService.updateQuantity(id, request.quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        cartService.removeItem(id);
        return ResponseEntity.noContent().build();
    }
}



