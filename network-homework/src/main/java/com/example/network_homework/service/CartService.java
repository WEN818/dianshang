package com.example.network_homework.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.network_homework.entity.CartItem;
import com.example.network_homework.entity.Product;
import com.example.network_homework.repository.CartItemRepository;
import com.example.network_homework.repository.ProductRepository;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getCartItems(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Transactional
    public CartItem addToCart(String userId, Long productId, Integer quantity) {
        Product product = productRepository.findActiveById(productId)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        
        if (product.getDeleted() != null && product.getDeleted()) {
            throw new IllegalArgumentException("商品已下架");
        }

        // 检查购物车中是否已存在该商品
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        
        if (existingItem.isPresent()) {
            // 如果已存在，更新数量（累加）
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartItemRepository.save(cartItem);
        } else {
            // 如果不存在，创建新的购物车项
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            return cartItemRepository.save(cartItem);
        }
    }
    
    @Transactional
    public void removeProductFromCarts(Long productId) {
        List<CartItem> cartItems = cartItemRepository.findByProductId(productId);
        cartItemRepository.deleteAll(cartItems);
    }

    @Transactional
    public CartItem updateQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("购物车项不存在"));
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    public void removeItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}



