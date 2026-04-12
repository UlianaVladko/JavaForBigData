package ru.bmstu.yabd.dto;

import jakarta.validation.constraints.*;

public record OrderRequest(
        @NotBlank String orderId,
        @NotBlank String product,
        @Min(1) int quantity,
        @Positive double price
) {}