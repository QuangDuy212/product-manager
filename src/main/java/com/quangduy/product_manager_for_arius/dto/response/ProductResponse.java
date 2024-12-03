package com.quangduy.product_manager_for_arius.dto.response;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonPropertyOrder(alphabetic = true)
public class ProductResponse {
    @JsonProperty("_id")
    String id;
    String name;
    String thumbnail;
    List<String> sliders;
    double price;
    String color;
    List<TagResponse> tags;
    CategoryResponse category;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
}
