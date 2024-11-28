package com.quangduy.product_manager_for_arius.entity;

import java.util.List;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "products")
@JsonPropertyOrder(alphabetic = true)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("_id")
    String id;
    String name;
    String shortDes;
    String thumbnail;
    List<String> sliders;
    double price;
    String color;
    @ManyToOne
    @JoinColumn(name = "category_name", nullable = true)
    Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_tag", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    List<Tag> tags;

    @OneToMany(mappedBy = "product")
    List<OrderDetail> orderDetails;
}
