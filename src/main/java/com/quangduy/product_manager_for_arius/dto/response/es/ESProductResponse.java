package com.quangduy.product_manager_for_arius.dto.response.es;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.quangduy.product_manager_for_arius.entity.es.ESCategory;
import com.quangduy.product_manager_for_arius.entity.es.ESTag;

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
public class ESProductResponse {
    @JsonProperty("_id")
    String id;
    String name;
    String thumbnail;
    List<String> sliders;
    double price;
    String color;
    ESCategory category;
    Set<ESTag> tags;
}
