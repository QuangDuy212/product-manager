package com.quangduy.product_manager_for_arius.entity.es;

import org.springframework.data.elasticsearch.annotations.Document;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Document(indexName = "tag")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ESTag {
    @Id
    String id;
    String name;
    String description;
}
