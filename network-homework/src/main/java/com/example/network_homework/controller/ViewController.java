package com.example.network_homework.controller;

import com.example.network_homework.entity.*;
import com.example.network_homework.repository.UserAccountRepository;
import com.example.network_homework.service.*;
import com.example.network_homework.util.OrderStatusUtil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class ViewController {

    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final UserAccountRepository userAccountRepository;

    public ViewController(ProductService productService, 
                         CartService cartService,
                         OrderService orderService,
                         UserAccountRepository userAccountRepository) {
        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/products/page")
    public String productsPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // 调试日志：确认当前用户
        if (userDetails != null) {
            System.out.println("==========================================");
            System.out.println("访问商品列表页面");
            System.out.println("当前用户: " + userDetails.getUsername());
            System.out.println("用户权限: " + userDetails.getAuthorities());
            System.out.println("==========================================");
        }
        
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("username", userDetails != null ? userDetails.getUsername() : null);
        
        // 判断是否是管理员
        boolean isAdmin = userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        
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
    
    @PostMapping("/cart/update")
    public String updateCartItem(@RequestParam("cartItemId") Long cartItemId,
                                 @RequestParam("quantity") Integer quantity) {
        cartService.updateQuantity(cartItemId, quantity);
        return "redirect:/cart/page";
    }
    
    @PostMapping("/cart/remove")
    public String removeCartItem(@RequestParam("cartItemId") Long cartItemId) {
        cartService.removeItem(cartItemId);
        return "redirect:/cart/page";
    }

    @GetMapping("/cart/page")
    public String cartPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        String userId = userDetails.getUsername();
        List<CartItem> items = cartService.getCartItems(userId);
        model.addAttribute("items", items);
        model.addAttribute("username", userId);
        
        // 判断是否是管理员
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        
        return "cart";
    }
    
    @GetMapping("/checkout/page")
    public String checkoutPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        String userId = userDetails.getUsername();
        List<CartItem> items = cartService.getCartItems(userId);
        if (items.isEmpty()) {
            return "redirect:/cart/page";
        }
        model.addAttribute("items", items);
        model.addAttribute("username", userId);
        
        // 获取用户邮箱
        Optional<UserAccount> userOpt = userAccountRepository.findByUsername(userId);
        userOpt.ifPresent(user -> model.addAttribute("userEmail", user.getEmail()));
        
        return "checkout";
    }
    
    @PostMapping("/orders/create")
    public String createOrder(@RequestParam("receiverName") String receiverName,
                             @RequestParam("receiverPhone") String receiverPhone,
                             @RequestParam("shippingAddress") String shippingAddress,
                             @RequestParam(value = "email", required = false) String email,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        String userId = userDetails.getUsername();
        List<CartItem> cartItems = cartService.getCartItems(userId);
        
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "购物车为空");
            return "redirect:/cart/page";
        }
        
        try {
            // 如果提供了新邮箱，更新用户邮箱
            if (email != null && !email.trim().isEmpty()) {
                Optional<UserAccount> userOpt = userAccountRepository.findByUsername(userId);
                userOpt.ifPresent(user -> {
                    user.setEmail(email);
                    userAccountRepository.save(user);
                });
            }
            
            Order order = orderService.createOrder(userId, receiverName, receiverPhone, 
                                                  shippingAddress, email, cartItems);
            redirectAttributes.addFlashAttribute("orderId", order.getId());
            return "redirect:/orders/page";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "创建订单失败：" + e.getMessage());
            return "redirect:/checkout/page";
        }
    }
    
    @GetMapping("/orders/page")
    public String ordersPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            String userId = userDetails.getUsername();
            List<Order> orders = orderService.getUserOrders(userId);
            model.addAttribute("orders", orders != null ? orders : List.of());
            model.addAttribute("username", userId);
            model.addAttribute("isAdmin", userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
            // 添加工具类到模型
            model.addAttribute("statusUtil", new OrderStatusUtil());
            return "orders";
        } catch (Exception e) {
            System.err.println("获取订单列表失败: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "获取订单列表失败，请稍后重试");
            model.addAttribute("orders", List.of());
            model.addAttribute("username", userDetails.getUsername());
            return "orders";
        }
    }
    
    @GetMapping("/orders/{orderId}")
    public String orderDetailPage(@PathVariable Long orderId,
                                  Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        try {
            Order order = orderService.getOrderById(orderId);
            boolean isOwner = order.getUserId().equals(userDetails.getUsername());
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (!isOwner && !isAdmin) {
                return "access-denied";
            }
            
            model.addAttribute("order", order);
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("statusUtil", new OrderStatusUtil());
            return "order-detail";
        } catch (Exception e) {
            return "redirect:/orders/page";
        }
    }
    
    @GetMapping("/admin")
    public String adminDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        if (!userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "access-denied";
        }
        try {
            OrderService.SalesStatistics stats = orderService.getSalesStatistics();
            List<Order> recentOrders = orderService.getAllOrders().stream()
                    .limit(10)
                    .toList();
            model.addAttribute("stats", stats);
            model.addAttribute("recentOrders", recentOrders != null ? recentOrders : List.of());
            model.addAttribute("username", userDetails.getUsername());
            // 添加工具类到模型
            model.addAttribute("statusUtil", new OrderStatusUtil());
            return "admin-dashboard";
        } catch (Exception e) {
            System.err.println("获取管理后台数据失败: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "获取数据失败，请稍后重试");
            model.addAttribute("stats", new OrderService.SalesStatistics(0, 0, 0.0));
            model.addAttribute("recentOrders", List.of());
            model.addAttribute("username", userDetails.getUsername());
            return "admin-dashboard";
        }
    }
    
    @GetMapping("/admin/orders")
    public String adminOrdersPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        if (!userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "access-denied";
        }
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("statusUtil", new OrderStatusUtil());
        return "admin-orders";
    }
    
    @GetMapping("/admin/customers")
    public String adminCustomersPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        if (!userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "access-denied";
        }
        List<UserAccount> customers = userAccountRepository.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("username", userDetails.getUsername());
        return "admin-customers";
    }
    
    @GetMapping("/admin/customers/{userId}")
    public String adminCustomerDetailPage(@PathVariable String userId,
                                         Model model,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        if (!userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "access-denied";
        }
        Optional<UserAccount> customerOpt = userAccountRepository.findByUsername(userId);
        if (customerOpt.isEmpty()) {
            return "redirect:/admin/customers";
        }
        UserAccount customer = customerOpt.get();
        List<Order> orders = orderService.getUserOrders(userId);
        model.addAttribute("customer", customer);
        model.addAttribute("orders", orders);
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("statusUtil", new OrderStatusUtil());
        return "admin-customer-detail";
    }
    
    @GetMapping("/admin/products")
    public String adminProductsPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        if (!userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "access-denied";
        }
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("username", userDetails.getUsername());
        return "admin-products";
    }
    
    @GetMapping("/deactivate/page")
    public String deactivatePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        // 管理员不能注销账户
        if (userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/products/page";
        }
        model.addAttribute("username", userDetails.getUsername());
        return "deactivate";
    }
    
    @GetMapping("/register/page")
    public String registerPage() {
        return "register";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}



