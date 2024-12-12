package com.quangduy.product_manager_for_arius.dto.response;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonPropertyOrder(alphabetic = true)
public class CartDetailResponse {
    @JsonProperty("_id")
    String id;
    long quantity;
    double price;
    Product product;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonPropertyOrder(alphabetic = true)
    public static class Product {
        @JsonProperty("_id")
        String id;
        String name;
        double price;
        String thumbnail;
        List<String> sliders;
    }
}
