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

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserDetails userDetails) {
        // 如果用户已登录，重定向到商品列表页面
        if (userDetails != null) {
            return "redirect:/products/page";
        }
        // 如果未登录，重定向到登录页面
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register/page")
    public String registerPage() {
        return "register";
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
            
            // 计算总价
            double totalPrice = items.stream()
                    .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                    .sum();
            
            model.addAttribute("items", items);
            model.addAttribute("username", userId);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("itemCount", items.size());
            model.addAttribute("isEmpty", items.isEmpty());
        } else {
            model.addAttribute("items", List.of());
            model.addAttribute("totalPrice", 0.0);
            model.addAttribute("itemCount", 0);
            model.addAttribute("isEmpty", true);
        }
        return "cart";
    }

    @PostMapping("/cart/increase")
    public String increaseQuantity(@RequestParam("cartItemId") Long cartItemId,
                                   @RequestParam(value = "increaseBy", defaultValue = "1") Integer increaseBy,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            cartService.increaseQuantity(cartItemId, increaseBy);
        }
        return "redirect:/cart/page";
    }

    @PostMapping("/cart/decrease")
    public String decreaseQuantity(@RequestParam("cartItemId") Long cartItemId,
                                   @RequestParam(value = "decreaseBy", defaultValue = "1") Integer decreaseBy,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            cartService.decreaseQuantity(cartItemId, decreaseBy);
        }
        return "redirect:/cart/page";
    }

    @PostMapping("/cart/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            String userId = userDetails.getUsername();
            cartService.clearCart(userId);
            model.addAttribute("checkoutSuccess", true);
        }
        return "redirect:/cart/page?success=true";
    }
    
    @GetMapping("/products/page")
    public String productsPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("username", userDetails != null ? userDetails.getUsername() : null);
        
        // 计算购物车商品数量
        if (userDetails != null) {
            String userId = userDetails.getUsername();
            List<CartItem> cartItems = cartService.getCartItems(userId);
            int totalQuantity = cartItems.stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();
            model.addAttribute("cartItemCount", totalQuantity);
        } else {
            model.addAttribute("cartItemCount", 0);
        }
        return "products";
    }
}



