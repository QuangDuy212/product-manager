package com.quangduy.product_manager_for_arius.entity.es;

import java.util.List;
import java.util.Set;

import org.springframework.data.elasticsearch.annotations.Document;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Document(indexName = "product")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ESProduct {
    @Id
    String id;
    String name;
    String thumbnail;
    List<String> sliders;
    double price;
    String color;
    ESCategory category;
    Set<ESTag> tags;
}
