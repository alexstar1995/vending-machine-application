package com.mvpfactory.vendingmachine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyResponse {

    private Integer totalSpent;
    private Integer numberOfProducts;
    private UUID productId;
    private List<Integer> change;
}
