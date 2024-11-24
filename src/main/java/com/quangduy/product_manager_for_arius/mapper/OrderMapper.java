package com.quangduy.product_manager_for_arius.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.quangduy.product_manager_for_arius.dto.request.OrderCreationRequest;
import com.quangduy.product_manager_for_arius.dto.request.OrderRequest;
import com.quangduy.product_manager_for_arius.dto.request.OrderUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.OrderResponse;
import com.quangduy.product_manager_for_arius.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toOrderResponse(Order order);

    Order toOrder(OrderRequest request);

    Order toOrder(OrderCreationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOrder(@MappingTarget Order order, OrderUpdateRequest request);
}
