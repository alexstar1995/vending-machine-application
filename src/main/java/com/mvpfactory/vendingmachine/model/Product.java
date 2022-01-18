package com.mvpfactory.vendingmachine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    private UUID id;

    @NotNull(message = "Mandatory amount")
    private Integer amountAvailable;

    @NotNull(message = "Mandatory cost")
    private Integer cost;

    @NotBlank(message = "Mandatory product name")
    private String productName;

    @NotNull(message = "Mandatory seller id")
    private UUID sellerId;
}
