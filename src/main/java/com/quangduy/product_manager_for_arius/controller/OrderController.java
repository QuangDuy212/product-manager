package com.quangduy.product_manager_for_arius.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quangduy.product_manager_for_arius.dto.request.OrderRequest;
import com.quangduy.product_manager_for_arius.dto.request.OrderUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.dto.response.OrderResponse;
import com.quangduy.product_manager_for_arius.service.OrderService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderController {
    OrderService orderService;

    @PostMapping
    ApiResponse<OrderResponse> create(@RequestBody @Valid OrderRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(this.orderService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<ApiPagination<OrderResponse>> getAllEntities(Pageable pageable) {
        return ApiResponse.<ApiPagination<OrderResponse>>builder()
                .result(this.orderService.getAllOrders(pageable))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<OrderResponse> getDetailEntity(
            @PathVariable("id") String id) {
        return ApiResponse.<OrderResponse>builder()
                .result(this.orderService.getDetailOrder(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<OrderResponse> update(@PathVariable("id") String id,
            @RequestBody @Valid OrderUpdateRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(this.orderService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> delete(@PathVariable("id") String id) {
        this.orderService.delete(id);
        return ApiResponse.<String>builder()
                .result("Delete success")
                .build();
    }
}
