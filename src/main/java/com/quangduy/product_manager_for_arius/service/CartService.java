package com.quangduy.product_manager_for_arius.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.quangduy.product_manager_for_arius.dto.request.CartRequest;
import com.quangduy.product_manager_for_arius.dto.response.CartResponse;
import com.quangduy.product_manager_for_arius.entity.Cart;
import com.quangduy.product_manager_for_arius.entity.CartDetail;
import com.quangduy.product_manager_for_arius.entity.Product;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.CartMapper;
import com.quangduy.product_manager_for_arius.repository.CartDetailRepository;
import com.quangduy.product_manager_for_arius.repository.CartRepository;
import com.quangduy.product_manager_for_arius.util.SecurityUtil;

import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartService {
    CartRepository cartRepository;
    UserService userService;
    ProductService productService;
    CartDetailService cartDetailService;
    CartMapper cartMapper;

    public CartResponse getCartByUser() {
        if (SecurityUtil.getCurrentUserLogin().isPresent() == false) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String username = SecurityUtil.getCurrentUserLogin().get();
        User user = this.userService.handleGetUserByUsername(username);
        Cart cart = this.cartRepository.findByUser(user);
        return this.cartMapper.toCartResponse(cart);
    }

    public void deleteCartById(String id) {
        this.cartRepository.deleteById(id);
    }

    public CartResponse handleAddProductToCart(CartRequest request) {
        if (SecurityUtil.getCurrentUserLogin().isPresent() == false) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String username = SecurityUtil.getCurrentUserLogin().get();
        User user = this.userService.handleGetUserByUsername(username);
        Cart cart = this.cartRepository.findByUser(user);
        if (cart == null) {
            cart = this.cartRepository.save(Cart.builder()
                    .user(user)
                    .sum(0)
                    .build());
        }
        Product product = this.productService.getProductById(request.getProductId());
        CartDetail cartDetail = this.cartDetailService.fetchByCartAndProduct(cart, product);
        List<CartDetail> old = cart.getCartDetails();
        if (cartDetail == null) {
            cartDetail = CartDetail.builder()
                    .cart(cart)
                    .price(product.getPrice())
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            if (old == null) {
                List<CartDetail> tmp = new ArrayList<>();
                tmp.add(cartDetail);
                old = tmp;
            } else
                old.add(cartDetail);
            cart.setCartDetails(old);
            int sum = cart.getSum() + 1;
            cart.setSum(sum);
        } else {
            long quantity = cartDetail.getQuantity() + request.getQuantity();
            cartDetail.setQuantity(quantity);
        }
        this.cartDetailService.save(cartDetail);
        this.cartRepository.save(cart);

        return this.cartMapper.toCartResponse(cart);
    }

    public CartResponse handleChangeQuantityInCart(CartRequest request) {
        if (SecurityUtil.getCurrentUserLogin().isPresent() == false) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String username = SecurityUtil.getCurrentUserLogin().get();
        User user = this.userService.handleGetUserByUsername(username);
        Cart cart = this.cartRepository.findByUser(user);
        if (cart == null) {
            cart = this.cartRepository.save(Cart.builder()
                    .user(user)
                    .sum(0)
                    .build());
        }
        Product product = this.productService.getProductById(request.getProductId());
        CartDetail cartDetail = this.cartDetailService.fetchByCartAndProduct(cart, product);

        long quantity = request.getQuantity();
        cartDetail.setQuantity(quantity);
        this.cartDetailService.save(cartDetail);
        this.cartRepository.save(cart);

        return this.cartMapper.toCartResponse(cart);
    }

    public CartResponse handleRemoveCartDetail(String id) {
        CartDetail cartDetail = this.cartDetailService.fetchById(id);
        Cart cart = cartDetail.getCart();
        String cartId = cart.getId();
        if (cart.getSum() > 1) {
            int sum = cart.getSum() - 1;
            cart.setSum(sum);
            this.cartRepository.save(cart);
            this.cartDetailService.deleteCartDetailById(id);
        } else {
            cart.setSum(0);
            this.cartRepository.save(cart);
            this.cartDetailService.deleteCartDetailById(id);
        }
        var res = this.cartMapper.toCartResponse(cart);
        return res;
    }
}
