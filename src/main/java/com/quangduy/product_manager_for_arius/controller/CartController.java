package com.quangduy.product_manager_for_arius.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quangduy.product_manager_for_arius.dto.request.CartRequest;
import com.quangduy.product_manager_for_arius.dto.request.CategoryRequest;
import com.quangduy.product_manager_for_arius.dto.response.CartResponse;
import com.quangduy.product_manager_for_arius.service.CartService;
import com.quangduy.product_manager_for_arius.util.annotation.ApiMessage;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @GetMapping
    @ApiMessage("Get cart by user success")
    ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok().body(this.cartService.getCartByUser());
    }

    @PostMapping("/add")
    @ApiMessage("Add product to cart success")
    ResponseEntity<CartResponse> addProductToCart(@RequestBody @Valid CartRequest request) {
        return ResponseEntity.ok().body(this.cartService.handleAddProductToCart(request));
    }

    @PostMapping("/delete/{id}")
    @ApiMessage("Delete cart detail from cart success")
    ResponseEntity<CartResponse> deleteProductFromCart(@PathVariable("id") String id) {
        return ResponseEntity.ok().body(this.cartService.handleRemoveCartDetail(id));
    }
}
