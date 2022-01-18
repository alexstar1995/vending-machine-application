package com.mvpfactory.vendingmachine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyResponse {

    private Integer totalSpent;
    private UUID productId;
    private List<Integer> change;
}
