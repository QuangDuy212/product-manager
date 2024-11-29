package com.quangduy.product_manager_for_arius.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @Builder.Default
    private int statusCode = 200;

    @Builder.Default
    private Object message = "CALL API SUCCESS";
    private String error;
    private T data;
}
