package com.quangduy.product_manager_for_arius.dto.request;

import java.util.List;

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
public class ProductUpdateRequest {
    String name;
    String thumbnail;
    List<String> sliders;
    double price;
    String shortDes;
    String categoryId;
    List<String> tagsId;
    double quantity;
    long discount;
}
