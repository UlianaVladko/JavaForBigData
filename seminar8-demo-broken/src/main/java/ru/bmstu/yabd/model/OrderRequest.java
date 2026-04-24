package ru.bmstu.yabd.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record OrderRequest(

        @NotBlank
        String orderId,

        @NotBlank
        String product,

        @Min(1)
        int quantity,

        @Min(0)
        double price

) {}