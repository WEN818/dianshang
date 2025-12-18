package com.example.network_homework.controller;

import com.example.network_homework.entity.CartItem;
import com.example.network_homework.entity.Product;
import com.example.network_homework.service.CartService;
import com.example.network_homework.service.ProductService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ViewController {

    private final ProductService productService;
    private final CartService cartService;

    public ViewController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/products/page")
    public String productsPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("username", userDetails != null ? userDetails.getUsername() : null);
        return "products";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") Integer quantity,
                            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String userId = userDetails.getUsername();
            cartService.addToCart(userId, productId, quantity);
        }
        return "redirect:/products/page";
    }

    @GetMapping("/cart/page")
    public String cartPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String userId = userDetails.getUsername();
            List<CartItem> items = cartService.getCartItems(userId);
            model.addAttribute("items", items);
            model.addAttribute("username", userId);
        }
        return "cart";
    }
}



