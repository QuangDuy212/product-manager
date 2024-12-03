package com.quangduy.product_manager_for_arius.entity.es;

import org.springframework.data.elasticsearch.annotations.Document;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Document(indexName = "category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ESCategory {
    @Id
    String id;
    String name;
}
