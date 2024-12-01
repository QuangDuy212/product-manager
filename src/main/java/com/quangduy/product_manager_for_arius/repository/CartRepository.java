package com.quangduy.product_manager_for_arius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quangduy.product_manager_for_arius.entity.Cart;
import com.quangduy.product_manager_for_arius.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    Cart findByUser(User user);
}
