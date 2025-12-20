package com.example.network_homework.repository;

import com.example.network_homework.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.deleted = false OR p.deleted IS NULL")
    List<Product> findAllActive();
    
    @Query("SELECT p FROM Product p WHERE (p.deleted = false OR p.deleted IS NULL) AND p.id = :id")
    Optional<Product> findActiveById(@Param("id") Long id);
}



