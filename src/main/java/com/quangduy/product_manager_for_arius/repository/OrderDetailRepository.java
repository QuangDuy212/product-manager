package com.quangduy.product_manager_for_arius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.quangduy.product_manager_for_arius.entity.OrderDetail;
import com.quangduy.product_manager_for_arius.entity.Permission;

@Repository
public interface OrderDetailRepository
        extends JpaRepository<OrderDetail, String>, JpaSpecificationExecutor<OrderDetail> {
    List<OrderDetail> findByIdIn(List<String> id);
}
