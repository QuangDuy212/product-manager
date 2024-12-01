package com.quangduy.product_manager_for_arius.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.quangduy.product_manager_for_arius.dto.request.CartRequest;
import com.quangduy.product_manager_for_arius.dto.response.CartResponse;
import com.quangduy.product_manager_for_arius.entity.Cart;

@Mapper(componentModel = "spring")
public interface CartMapper {
    Cart toCart(CartRequest request);

    CartResponse toCartResponse(Cart cart);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCart(@MappingTarget Cart cart, CartRequest request);
}
