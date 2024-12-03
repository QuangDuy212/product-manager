package com.quangduy.product_manager_for_arius.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.dto.request.OrderCreationRequest;
import com.quangduy.product_manager_for_arius.dto.request.OrderRequest;
import com.quangduy.product_manager_for_arius.dto.request.OrderUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.OrderDetailResponse;
import com.quangduy.product_manager_for_arius.dto.response.OrderResponse;
import com.quangduy.product_manager_for_arius.dto.response.ProductResponse;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.entity.Order;
import com.quangduy.product_manager_for_arius.entity.OrderDetail;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.OrderDetailMapper;
import com.quangduy.product_manager_for_arius.mapper.OrderMapper;
import com.quangduy.product_manager_for_arius.mapper.ProductMapper;
import com.quangduy.product_manager_for_arius.repository.OrderDetailRepository;
import com.quangduy.product_manager_for_arius.repository.OrderRepository;
import com.quangduy.product_manager_for_arius.repository.ProductRepository;
import com.quangduy.product_manager_for_arius.repository.UserRepository;
import com.quangduy.product_manager_for_arius.util.SecurityUtil;
import com.turkraft.springfilter.boot.Filter;

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
        OrderDetailMapper orderDetailMapper;
        ProductMapper productMapper;
        OrderRepository orderRepository;
        UserRepository userRepository;
        OrderDetailRepository orderDetailRepository;
        ProductRepository productRepository;

        public OrderResponse create(OrderCreationRequest request) {
                log.info("Create a order");
                Order order = this.orderMapper.toOrder(request);
                if (request.getUserId() != null) {
                        User user = this.userRepository.findById(request.getUserId())
                                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                        order.setUser(user);
                }
                Order entity = this.orderRepository.save(order);
                List<OrderDetail> listOrderDetails = request.getDetail().stream()
                                .map(i -> {
                                        Product product = this.productRepository.findById(i.getId())
                                                        .orElseThrow(() -> new AppException(
                                                                        ErrorCode.PRODUCT_NOT_FOUND));
                                        OrderDetail orderDetail = OrderDetail.builder()
                                                        .quantity(i.getQuantity())
                                                        .price(product.getPrice())
                                                        .order(entity)
                                                        .product(product)
                                                        .build();
                                        return this.orderDetailRepository.save(orderDetail);
                                }).toList();
                List<OrderDetailResponse> orderDetails = listOrderDetails.stream()
                                .map(orderDetailMapper::toOrderDetailResponse).toList();
                OrderResponse res = this.orderMapper.toOrderResponse(entity);
                res.setOrderDetails(orderDetails);
                return res;
        }

        public OrderResponse update(String id, OrderUpdateRequest request) {
                log.info("Update a order");
                Order dataDB = this.orderRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
                this.orderMapper.updateOrder(dataDB, request);
                return this.orderMapper.toOrderResponse(this.orderRepository.save(dataDB));
        }

        public OrderResponse getDetailOrder(String id) {
                log.info("Get detail order");
                Order dataDB = this.orderRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
                return this.orderMapper.toOrderResponse(dataDB);
        }

        public ApiPagination<OrderResponse> getAllOrders(@Filter Specification<Order> spec, Pageable pageable) {
                log.info("Get all orders");
                Page<Order> pageCategories = this.orderRepository.findAll(spec, pageable);

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

        public List<OrderResponse> getAllOrders() {
                log.info("Get all orders");
                List<Order> entities = this.orderRepository.findAll();
                List<OrderResponse> res = entities.stream().map(orderMapper::toOrderResponse).toList();
                return res;
        }

        public ApiPagination<OrderResponse> getHistory(Pageable pageable) {
                log.info("Get all orders");
                if (SecurityUtil.getCurrentUserLogin().isPresent() == false) {
                        throw new AppException(ErrorCode.UNAUTHENTICATED);
                }
                String username = SecurityUtil.getCurrentUserLogin().get();
                User user = this.userRepository.findByUsername(username)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                Page<Order> pageCategories = this.orderRepository.findByUser(user, pageable);

                List<OrderResponse> list = pageCategories.getContent().stream()
                                .map((item) -> {
                                        OrderResponse res = this.orderMapper.toOrderResponse(item);
                                        List<OrderDetailResponse> orderDetails = item.getOrderDetails().stream()
                                                        .map(orderDetailMapper::toOrderDetailResponse).toList();
                                        res.setOrderDetails(orderDetails);
                                        return res;
                                }).toList();

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
