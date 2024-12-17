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
import com.quangduy.product_manager_for_arius.entity.Cart;
import com.quangduy.product_manager_for_arius.entity.CartDetail;
import com.quangduy.product_manager_for_arius.entity.Order;
import com.quangduy.product_manager_for_arius.entity.OrderDetail;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.OrderDetailMapper;
import com.quangduy.product_manager_for_arius.mapper.OrderMapper;
import com.quangduy.product_manager_for_arius.mapper.ProductMapper;
import com.quangduy.product_manager_for_arius.repository.CartDetailRepository;
import com.quangduy.product_manager_for_arius.repository.CartRepository;
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
        CartRepository cartRepository;
        CartDetailRepository cartDetailRepository;

        public OrderResponse create(OrderCreationRequest request) {
                log.info("Create a order");
                Order order = this.orderMapper.toOrder(request);
                List<String> ids = request.getDetail().stream().map(i -> i.getId()).toList();
                List<CartDetail> cd = this.cartDetailRepository.findByIdIn(ids);

                User user = this.userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                order.setUser(user);
                Cart cart = this.cartRepository.findByUser(user);
                int newSum = cart.getSum() - request.getDetail().size();
                cart.setSum(newSum);
                this.cartRepository.save(cart);
                // List<CartDetail> newCD = cart.getCartDetails().stream()
                // .filter(i -> !cd.contains(i))
                // .toList();
                // cart.setCartDetails(newCD);
                cd.forEach(i -> {
                        i.setCart(null);
                        this.cartDetailRepository.save(i);
                });
                Order entity = this.orderRepository.save(order);
                List<OrderDetail> listOrderDetails = request.getDetail().stream()
                                .map(i -> {
                                        CartDetail cdLocal = this.cartDetailRepository.findById(i.getId())
                                                        .orElseThrow(() -> new AppException(
                                                                        ErrorCode.CARTDETAIL_NOT_EXISTED));
                                        Product product = cdLocal.getProduct();
                                        long newQ = product.getQuantity() - cdLocal.getQuantity();
                                        product.setQuantity(newQ);
                                        productRepository.save(product);
                                        OrderDetail orderDetail = OrderDetail.builder()
                                                        .quantity(cdLocal.getQuantity())
                                                        .price(product.getPrice())
                                                        .order(entity)
                                                        .product(product)
                                                        .build();
                                        return this.orderDetailRepository.save(orderDetail);
                                }).toList();
                this.cartDetailRepository.deleteAll(cd);

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
                Order order = this.orderRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

                List<OrderDetail> a = order.getOrderDetails();
                this.orderDetailRepository.deleteAll(a);
                this.orderRepository.deleteById(id);
        }

        public void deleteAll(List<String> ids) {
                log.info("Delete list orders");
                List<Order> orders = this.orderRepository.findByIdIn(ids);
                orders.forEach(i -> {
                        this.orderDetailRepository.deleteAll(i.getOrderDetails());
                });
                this.orderRepository.deleteAll(orders);
        }

}
