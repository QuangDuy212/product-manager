package com.quangduy.product_manager_for_arius.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.Response;
import com.quangduy.product_manager_for_arius.dto.request.OrderDetailRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.dto.response.OrderDetailResponse;
import com.quangduy.product_manager_for_arius.service.OrderDetailService;
import com.quangduy.product_manager_for_arius.util.annotation.ApiMessage;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/orderdetails")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderDetailController {
    OrderDetailService orderDetailService;

    @PostMapping
    @ApiMessage("Create a order detail success")
    ResponseEntity<OrderDetailResponse> create(@RequestBody @Valid OrderDetailRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderDetailService.create(request));
    }

    @GetMapping
    @ApiMessage("Get all orders detail success")
    ResponseEntity<ApiPagination<OrderDetailResponse>> getAllEntities(Pageable pageable) {
        return ResponseEntity.ok().body(this.orderDetailService.getAllOrders(pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get a order detail success")
    ResponseEntity<OrderDetailResponse> getDetailEntity(
            @PathVariable("id") String id) {
        return ResponseEntity.ok().body(this.orderDetailService.getDetailOrder(id));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update a order detail success")
    ResponseEntity<OrderDetailResponse> update(@PathVariable("id") String id,
            @RequestBody @Valid OrderDetailRequest request) {
        return ResponseEntity.ok().body(this.orderDetailService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a order detail success")
    ResponseEntity<String> delete(@PathVariable("id") String id) {
        this.orderDetailService.delete(id);
        return ResponseEntity.ok().body("ok");
    }
}
