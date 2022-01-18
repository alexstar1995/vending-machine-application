package com.mvpfactory.vendingmachine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    private UUID id;

    @NotBlank(message = "Mandatory username")
    private Integer amountAvailable;

    @NotBlank(message = "Mandatory username")
    private Integer cost;

    @NotBlank(message = "Mandatory username")
    private String productName;

    @NotBlank(message = "Mandatory username")
    private UUID sellerId;
}
