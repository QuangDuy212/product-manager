package com.quangduy.product_manager_for_arius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.quangduy.product_manager_for_arius.entity.Cart;
import com.quangduy.product_manager_for_arius.entity.CartDetail;
import com.quangduy.product_manager_for_arius.entity.Product;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, String>, JpaSpecificationExecutor<CartDetail> {
    CartDetail findByCartAndProduct(Cart cart, Product product);

    List<CartDetail> findByIdIn(List<String> ids);
}
