package ru.bmstu.yabd.dto;

import jakarta.validation.constraints.*;

public record UpdateOrderRequest(
        @NotBlank(message = "Product must not be blank")
        String product,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity,

        @Positive(message = "Price must be positive")
        double price,

        @Pattern(
                regexp = "NEW|PROCESSING|COMPLETED",
                message = "Status must be NEW, PROCESSING or COMPLETED"
        )
        String status
) {}