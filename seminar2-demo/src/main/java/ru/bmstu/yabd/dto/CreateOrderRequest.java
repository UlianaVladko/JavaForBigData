package ru.bmstu.yabd.dto;

import jakarta.validation.constraints.*;

public record CreateOrderRequest(
        @NotBlank(message = "Product must not be blank")
        String product,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        double price
) {}