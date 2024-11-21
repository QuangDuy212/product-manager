package com.quangduy.product_manager_for_arius.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quangduy.product_manager_for_arius.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
