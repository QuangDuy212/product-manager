package com.quangduy.product_manager_for_arius.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.dto.request.OrderRequest;
import com.quangduy.product_manager_for_arius.dto.request.OrderUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.OrderResponse;
import com.quangduy.product_manager_for_arius.entity.Order;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.OrderMapper;
import com.quangduy.product_manager_for_arius.repository.OrderRepository;
import com.quangduy.product_manager_for_arius.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {
    OrderMapper orderMapper;
    OrderRepository orderRepository;
    UserRepository userRepository;

    public OrderResponse create(OrderRequest request) {
        log.info("Create a order");
        Order order = this.orderMapper.toOrder(request);
        if (request.getUserId() != null) {
            User user = this.userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            order.setUser(user);
        }
        return this.orderMapper.toOrderResponse(this.orderRepository.save(order));
    }

    public OrderResponse update(String id, OrderUpdateRequest request) {
        log.info("Update a order");
        Order dataDB = this.orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        this.orderMapper.updateOrder(dataDB, request);
        return this.orderMapper.toOrderResponse(this.orderRepository.save(dataDB));
    }

    public OrderResponse getDetailOrder(String id) {
        log.info("Get detail order");
        Order dataDB = this.orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return this.orderMapper.toOrderResponse(dataDB);
    }

    public ApiPagination<OrderResponse> getAllOrders(Pageable pageable) {
        log.info("Get all categories");
        Page<Order> pageCategories = this.orderRepository.findAll(pageable);

        List<OrderResponse> list = pageCategories.getContent().stream()
                .map(orderMapper::toOrderResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();

        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageCategories.getTotalPages());
        mt.setTotal(pageCategories.getTotalElements());

        return ApiPagination.<OrderResponse>builder()
                .meta(mt)
                .result(list)
                .build();
    }

    public void delete(String id) {
        log.info("Delete a order");
        this.orderRepository.deleteById(id);
    }
}
