package com.quangduy.product_manager_for_arius.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonPropertyOrder(alphabetic = true)
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @NotBlank(message = "Name không được để trống")
    String name;
    @NotBlank(message = "Api path không được để trống")
    String apiPath;
    @NotBlank(message = "Method không được để trống")
    String method;
    @NotBlank(message = "Module không được để trống")
    String module;

    boolean active;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    @JsonIgnore
    List<Role> roles;

    public Permission(@NotBlank(message = "Name không được để trống") String name,
            @NotBlank(message = "Api path không được để trống") String apiPath,
            @NotBlank(message = "Method không được để trống") String method,
            @NotBlank(message = "Module không được để trống") String module, boolean active) {
        this.name = name;
        this.apiPath = apiPath;
        this.method = method;
        this.module = module;
        this.active = active;
    }

}
