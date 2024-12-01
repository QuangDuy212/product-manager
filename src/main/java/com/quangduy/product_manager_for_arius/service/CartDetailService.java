package com.quangduy.product_manager_for_arius.service;

import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.entity.Cart;
import com.quangduy.product_manager_for_arius.entity.CartDetail;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.repository.CartDetailRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartDetailService {
    CartDetailRepository cartDetailRepository;

    public CartDetail fetchByCartAndProduct(Cart cart, Product product) {
        return this.cartDetailRepository.findByCartAndProduct(cart, product);
    }

    public CartDetail fetchById(String id) {
        return this.cartDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CARTDETAIL_NOT_EXISTED));
    }

    public CartDetail save(CartDetail entity) {
        return this.cartDetailRepository.save(entity);
    }

    public void deleteCartDetailById(String id) {
        this.cartDetailRepository.deleteById(id);
    }
}
