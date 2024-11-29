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

import com.quangduy.product_manager_for_arius.dto.request.OrderDetailRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.dto.response.OrderDetailResponse;
import com.quangduy.product_manager_for_arius.service.OrderDetailService;

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
    ApiResponse<OrderDetailResponse> create(@RequestBody @Valid OrderDetailRequest request) {
        return ApiResponse.<OrderDetailResponse>builder()
                .data(this.orderDetailService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<ApiPagination<OrderDetailResponse>> getAllEntities(Pageable pageable) {
        return ApiResponse.<ApiPagination<OrderDetailResponse>>builder()
                .data(this.orderDetailService.getAllOrders(pageable))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<OrderDetailResponse> getDetailEntity(
            @PathVariable("id") String id) {
        return ApiResponse.<OrderDetailResponse>builder()
                .data(this.orderDetailService.getDetailOrder(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<OrderDetailResponse> update(@PathVariable("id") String id,
            @RequestBody @Valid OrderDetailRequest request) {
        return ApiResponse.<OrderDetailResponse>builder()
                .data(this.orderDetailService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> delete(@PathVariable("id") String id) {
        this.orderDetailService.delete(id);
        return ApiResponse.<String>builder()
                .data("Delete success")
                .build();
    }
}
