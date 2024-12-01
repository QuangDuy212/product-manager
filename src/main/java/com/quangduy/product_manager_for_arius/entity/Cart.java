package com.quangduy.product_manager_for_arius.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "carts")
@JsonPropertyOrder(alphabetic = true)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Min(value = 0)
    int sum;

    @OneToOne()
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "cart")
    List<CartDetail> cartDetails;
}
