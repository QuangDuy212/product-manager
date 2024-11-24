package com.quangduy.product_manager_for_arius.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.quangduy.product_manager_for_arius.dto.request.OrderDetailRequest;
import com.quangduy.product_manager_for_arius.dto.response.OrderDetailResponse;
import com.quangduy.product_manager_for_arius.entity.OrderDetail;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {
    OrderDetail toOrderDetail(OrderDetailRequest request);

    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOrderDetail(@MappingTarget OrderDetail orderDetail, OrderDetailRequest request);
}
