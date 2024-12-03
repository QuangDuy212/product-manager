package com.quangduy.product_manager_for_arius.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.dto.request.OrderDetailRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.OrderDetailResponse;
import com.quangduy.product_manager_for_arius.entity.Order;
import com.quangduy.product_manager_for_arius.entity.OrderDetail;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.OrderDetailMapper;
import com.quangduy.product_manager_for_arius.repository.OrderDetailRepository;
import com.quangduy.product_manager_for_arius.repository.OrderRepository;
import com.quangduy.product_manager_for_arius.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderDetailService {
    OrderDetailRepository orderDetailRepository;
    OrderRepository orderRepository;
    ProductRepository productRepository;
    OrderDetailMapper orderDetailMapper;

    public OrderDetailResponse create(OrderDetailRequest request) {
        log.info("Create a order detail");
        OrderDetail entity = this.orderDetailMapper.toOrderDetail(request);
        if (request.getProductId() != null) {
            Product product = this.productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            entity.setProduct(product);
        }
        if (request.getOrderId() != null) {
            Order order = this.orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
            entity.setOrder(order);
        }
        return this.orderDetailMapper.toOrderDetailResponse(this.orderDetailRepository.save(entity));
    }

    public OrderDetailResponse update(String id, OrderDetailRequest request) {
        log.info("Update a order detail");
        OrderDetail dataDB = this.orderDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        this.orderDetailMapper.updateOrderDetail(dataDB, request);
        return this.orderDetailMapper.toOrderDetailResponse(this.orderDetailRepository.save(dataDB));
    }

    public OrderDetailResponse getDetailOrder(String id) {
        log.info("Get detail order detail");
        OrderDetail dataDB = this.orderDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return this.orderDetailMapper.toOrderDetailResponse(dataDB);
    }

    public ApiPagination<OrderDetailResponse> getAllOrders(Specification<OrderDetail> spec, Pageable pageable) {
        log.info("Get all order details");
        Page<OrderDetail> pageCategories = this.orderDetailRepository.findAll(spec, pageable);

        List<OrderDetailResponse> list = pageCategories.getContent().stream()
                .map(orderDetailMapper::toOrderDetailResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();

        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageCategories.getTotalPages());
        mt.setTotal(pageCategories.getTotalElements());

        return ApiPagination.<OrderDetailResponse>builder()
                .meta(mt)
                .result(list)
                .build();
    }

    public void delete(String id) {
        log.info("Delete a order detail");
        this.orderDetailRepository.deleteById(id);
    }
}
