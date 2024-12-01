package com.quangduy.product_manager_for_arius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quangduy.product_manager_for_arius.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    List<Permission> findByIdIn(List<String> listId);
}
