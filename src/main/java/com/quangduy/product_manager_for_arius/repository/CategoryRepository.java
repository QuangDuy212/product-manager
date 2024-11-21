package com.quangduy.product_manager_for_arius.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quangduy.product_manager_for_arius.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
