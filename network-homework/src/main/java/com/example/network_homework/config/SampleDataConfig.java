package com.example.network_homework.config;

import com.example.network_homework.entity.Product;
import com.example.network_homework.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleDataConfig {

    @Bean
    public CommandLineRunner initSampleProducts(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() == 0) {
                Product p1 = new Product();
                p1.setName("示例商品1");
                p1.setDescription("这是第一个示例商品");
                p1.setPrice(99.9);
                p1.setStock(100);

                Product p2 = new Product();
                p2.setName("示例商品2");
                p2.setDescription("这是第二个示例商品");
                p2.setPrice(199.0);
                p2.setStock(50);

                productRepository.save(p1);
                productRepository.save(p2);
            }
        };
    }
}



