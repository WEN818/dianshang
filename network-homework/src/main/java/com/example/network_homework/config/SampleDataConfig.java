package com.example.network_homework.config;

import com.example.network_homework.entity.Product;
import com.example.network_homework.entity.UserAccount;
import com.example.network_homework.repository.ProductRepository;
import com.example.network_homework.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SampleDataConfig {

    @Bean
    public CommandLineRunner initSampleData(ProductRepository productRepository,
                                           UserAccountRepository userAccountRepository,
                                           PasswordEncoder passwordEncoder) {
        return args -> {
            // 初始化示例商品
            if (productRepository.count() == 0) {
                Product p1 = new Product();
                p1.setName("示例商品1");
                p1.setDescription("这是第一个示例商品");
                p1.setPrice(99.9);
                p1.setStock(100);
                p1.setDeleted(false);

                Product p2 = new Product();
                p2.setName("示例商品2");
                p2.setDescription("这是第二个示例商品");
                p2.setPrice(199.0);
                p2.setStock(50);
                p2.setDeleted(false);

                productRepository.save(p1);
                productRepository.save(p2);
            }
            
            // 初始化管理员账户
            if (!userAccountRepository.findByUsername("admin").isPresent()) {
                UserAccount admin = new UserAccount();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                admin.setEmail("admin@example.com");
                admin.setEnabled(true);
                userAccountRepository.save(admin);
            }
        };
    }
}



