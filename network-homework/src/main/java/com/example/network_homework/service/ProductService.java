package com.example.network_homework.service;

import com.example.network_homework.entity.Product;
import com.example.network_homework.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAllActive();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findActiveById(id);
    }
    
    public Optional<Product> findByIdAndLog(Long id, String userId) {
        return findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        product.setDeleted(true);
        productRepository.save(product);
    }
}



