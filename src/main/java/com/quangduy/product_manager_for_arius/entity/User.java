package com.quangduy.product_manager_for_arius.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Entity(name = "users")
@JsonPropertyOrder(alphabetic = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("_id")
    String id;

    @Column(columnDefinition = "TEXT")
    String refreshToken;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255)")
    String username;
    String password;
    String firstName;
    String lastName;
    String address;

    @OneToMany(mappedBy = "user")
    List<Order> orders;

    @ManyToOne
    @JoinColumn(name = "role_name")
    Role role;
}
