package com.quangduy.product_manager_for_arius.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "orders")
@JsonPropertyOrder(alphabetic = true)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("_id")
    String id;
    double totalPrice;
    String reciverName;
    String reciverAddress;
    @Pattern(regexp = "(^$|[0-9]{10})")
    String reciverPhone;
    String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
