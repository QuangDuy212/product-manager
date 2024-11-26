package com.quangduy.product_manager_for_arius.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quangduy.product_manager_for_arius.dto.request.OrderCreationRequest;
import com.quangduy.product_manager_for_arius.dto.request.OrderRequest;
import com.quangduy.product_manager_for_arius.dto.request.OrderUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.dto.response.OrderResponse;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.service.OrderService;
import com.quangduy.product_manager_for_arius.service.export.OrderExcelExporter;
import com.quangduy.product_manager_for_arius.service.export.UserExcelExporter;

import jakarta.servlet.http.HttpServletResponse;
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
    ApiResponse<OrderResponse> create(@RequestBody @Valid OrderCreationRequest request) {
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

    @GetMapping("/history")
    ApiResponse<ApiPagination<OrderResponse>> getHistory(Pageable pageable) {
        return ApiResponse.<ApiPagination<OrderResponse>>builder()
                .result(this.orderService.getHistory(pageable))
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

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<OrderResponse> data = this.orderService.getAllOrders();

        OrderExcelExporter excelExporter = new OrderExcelExporter(data);

        excelExporter.export(response);
    }
}
