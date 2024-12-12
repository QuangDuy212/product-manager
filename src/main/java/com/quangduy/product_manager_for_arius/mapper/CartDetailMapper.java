package com.quangduy.product_manager_for_arius.mapper;

import org.mapstruct.Mapper;

import com.quangduy.product_manager_for_arius.dto.response.CartDetailResponse;
import com.quangduy.product_manager_for_arius.entity.CartDetail;

@Mapper(componentModel = "spring")
public interface CartDetailMapper {
    CartDetailResponse toCartDetailResponse(CartDetail request);
}
