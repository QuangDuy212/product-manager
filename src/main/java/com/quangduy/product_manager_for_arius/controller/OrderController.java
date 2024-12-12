package com.quangduy.product_manager_for_arius.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

import com.quangduy.product_manager_for_arius.dto.request.DeleteAllRequest;
import com.quangduy.product_manager_for_arius.dto.request.OrderCreationRequest;
import com.quangduy.product_manager_for_arius.dto.request.OrderRequest;
import com.quangduy.product_manager_for_arius.dto.request.OrderUpdateRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiPagination;
import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.dto.response.ApiString;
import com.quangduy.product_manager_for_arius.dto.response.OrderResponse;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.entity.Order;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.service.OrderService;
import com.quangduy.product_manager_for_arius.service.export.OrderExcelExporter;
import com.quangduy.product_manager_for_arius.service.export.UserExcelExporter;
import com.quangduy.product_manager_for_arius.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

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
    @ApiMessage("Create a order success")
    ResponseEntity<OrderResponse> create(@RequestBody @Valid OrderCreationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.create(request));
    }

    @GetMapping
    @ApiMessage("Get all orders success")
    ResponseEntity<ApiPagination<OrderResponse>> getAllEntities(@Filter Specification<Order> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.orderService.getAllOrders(spec, pageable));
    }

    @GetMapping("/history")
    @ApiMessage("Get history success")
    ResponseEntity<ApiPagination<OrderResponse>> getHistory(Pageable pageable) {
        return ResponseEntity.ok().body(this.orderService.getHistory(pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get detail order success")
    ResponseEntity<OrderResponse> getDetailEntity(
            @PathVariable("id") String id) {
        return ResponseEntity.ok().body(this.orderService.getDetailOrder(id));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update a order success")
    ResponseEntity<OrderResponse> update(@PathVariable("id") String id,
            @RequestBody @Valid OrderUpdateRequest request) {
        return ResponseEntity.ok().body(this.orderService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a order success")
    ResponseEntity<ApiString> delete(@PathVariable("id") String id) {
        this.orderService.delete(id);
        return ResponseEntity.ok().body(ApiString.builder()
                .message("success")
                .build());
    }

    @DeleteMapping("/deleteAll")
    @ApiMessage("Delete list orders success")
    ResponseEntity<ApiString> deleteListOrder(
            @RequestBody DeleteAllRequest request) {
        this.orderService.deleteAll(request.getIds());
        return ResponseEntity.ok().body(ApiString.builder()
                .message("success")
                .build());
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
